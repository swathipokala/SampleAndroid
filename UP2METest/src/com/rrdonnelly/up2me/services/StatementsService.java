package com.rrdonnelly.up2me.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.rrdonnelly.up2me.R;
import com.rrdonnelly.up2me.dao.DocumentProvidersDAO;
import com.rrdonnelly.up2me.dao.OfferProvidersDAO;
import com.rrdonnelly.up2me.dao.StatementDAO;
import com.rrdonnelly.up2me.json.DocumentProviders;
import com.rrdonnelly.up2me.json.Image;
import com.rrdonnelly.up2me.json.MarkItemChecked;
import com.rrdonnelly.up2me.json.OfferProviders;
import com.rrdonnelly.up2me.json.Statement;
import com.rrdonnelly.up2me.json.SubAccount;
import com.rrdonnelly.up2me.json.TelephoneStatement;
import com.rrdonnelly.up2me.util.AsyncImageDownloader;
import com.rrdonnelly.up2me.util.ImageUtil;
import com.rrdonnelly.up2me.valueobjects.MarkDocumentDelete;
import com.rrdonnelly.up2me.valueobjects.MarkPaidOrUnpaid;
import com.rrdonnelly.up2me.valueobjects.MarkReadOrUnRead;
import com.rrdonnelly.up2me.valueobjects.UserFavorite;

public class StatementsService {
	static ArrayList<String> arrImagePath = new ArrayList<String>();
	public static void syncStatements(Context currentActivity,
			String userToken, String userName, String salt, long loginUserId,
			long lastSyncDateTime) throws ClientProtocolException, IOException,
			JSONException {

		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = currentActivity.getResources().getString(
				R.string.webservice_url);

		String statementWebServiceUrl = webServiceUrl
				+ "/user/Statements?userId=" + loginUserId;
		System.out.println("LastSyncDateTime:" + lastSyncDateTime);
		if (lastSyncDateTime != 0l) {
			statementWebServiceUrl += "&date=" + lastSyncDateTime;
		}

		HttpGet request = new HttpGet(statementWebServiceUrl);
		request.addHeader("userName", userName);
		request.addHeader("userToken", userToken);
		request.addHeader("salt", salt);

		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		StringBuilder responseJSONText = new StringBuilder();

		String line = "";
		while ((line = rd.readLine()) != null) {
			responseJSONText.append(line);
		}

		// Gson gson = new GsonBuilder().serializeNulls().create();
		// Object value = new Gson().fromJson(responseJSONText.toString(), new
		// TypeToken<List<Offer>>() {}.getType());
		/*
		 * Map<String,List<Statement>> statements = new
		 * Gson().fromJson(responseJSONText.toString(), new
		 * TypeToken<Map<String,List<Statement>>>() {}.getType());
		 * 
		 * List<Statement> billStatements = statements.get("Bill");
		 * List<Statement> accountStatements = statements.get("Account");
		 * 
		 * Log.d("Insert: ", "Inserting Statements.."); StatementDAO
		 * statementDAO = new StatementDAO();
		 * 
		 * statementDAO.refresh();
		 * 
		 * for(Statement billStatement: billStatements){
		 * statementDAO.addStatement(billStatement); }
		 * 
		 * for(Statement accountStatement: accountStatements){
		 * statementDAO.addStatement(accountStatement); }
		 * 
		 * Log.d("Insert: ", "Inserting Statements Completed.");
		 */

		Map<String, List<Statement>> statementsMap = new HashMap<String, List<Statement>>();

		List<Statement> billStatements = new ArrayList<Statement>();
		List<Statement> accountStatements = new ArrayList<Statement>();

		JSONObject json = new JSONObject(responseJSONText.toString());

		billStatements = createStatementList("Bill", json, currentActivity);
		accountStatements = createStatementList("Statement", json,
				currentActivity);
		
		if (arrImagePath !=null && arrImagePath.size()>0)
		new AsyncImageDownloader(currentActivity).execute(arrImagePath);
		

		String statementIds = "";
		for (Statement billStatement : billStatements) {
			statementIds = statementIds + billStatement.getId() + ",";
		}

		for (Statement accountStatement : accountStatements) {
			statementIds = statementIds + accountStatement.getId() + ",";
		}

		if (statementIds.length() > 0) {
			statementIds = statementIds.substring(0,
					statementIds.lastIndexOf(","));
		}

		// System.out.println(statementIds);

		statementsMap.put("Bill", billStatements);
		statementsMap.put("Statement", accountStatements);

		Log.d("Insert: ", "Inserting Statements..");
		// refresh(currentActivity);
		StatementDAO.deleteStatements(statementIds, currentActivity,
				loginUserId);

		for (Statement billStatement : billStatements) {
			StatementDAO.addStatement(billStatement, loginUserId,
					currentActivity);
		}

		for (Statement accountStatement : accountStatements) {
			StatementDAO.addStatement(accountStatement, loginUserId,
					currentActivity);
		}

		Log.d("Insert: ", "Inserting Statements Completed.");
	}

	public static List<Statement> createStatementList(String type,
			JSONObject json, Context activity) throws JSONException {
		
		
		JSONArray billsArray = (JSONArray) json.get(type);
		List<Statement> statementsList = new ArrayList<Statement>();

		for (int i = 0; i < billsArray.length(); i++) {
			JSONObject bill = billsArray.getJSONObject(i);

			Statement statement = new Statement();

			List<SubAccount> subAccountsList = new ArrayList<SubAccount>();
			// List<DocTransaction> docTransactionList = new
			// ArrayList<DocTransaction>();
			List<TelephoneStatement> telephoneStatementsList = new ArrayList<TelephoneStatement>();

			TelephoneStatement telephoneStatement = new TelephoneStatement();

			if (bill != null) {
				// Level two statements (Bill -> Statement*)

				statement.setName(bill.getString("name"));
				statement.setDocumentPath(bill.getString("documentPath"));
				statement.setMessage(bill.getString("message"));
				statement.setMessageType(bill.getString("messageType"));
				statement.setStatementDisplayDate(bill
						.getString("statementDisplayDate"));
				statement.setPeriodEndDate(bill.getString("periodEndDate"));
				statement.setDueDate(bill.getString("dueDate"));
				statement
						.setMinAmountDue((bill.get("minAmountDue")) != JSONObject.NULL ? bill
								.getDouble("minAmountDue") : 0.0);
				statement
						.setBalance((bill.get("balance")) != JSONObject.NULL ? bill
								.getDouble("balance") : 0.0);
				statement.setProviderName(bill.getString("providerName"));
				statement.setValid(bill.getBoolean("valid"));
				statement.setId((bill.get("id")) != JSONObject.NULL ? bill
						.getLong("id") : 0l);
				statement.setRead(bill.getBoolean("read"));
				statement.setPaid(bill.getBoolean("paid"));
				statement.setBill(bill.getBoolean("bill"));
				statement.setPDFDeleted(bill.getBoolean("delete"));
				
				statement.setDocInBill(bill.getBoolean("docInBill"));
				statement.setDocInCashflow(bill.getBoolean("docInCashflow"));
				statement.setDocInStatement(bill.getBoolean("docInStatement"));
				statement.setDocInUnPaid(bill.getBoolean("docInUnPaid"));
				
				
				statement.setUserPrimaryEmail(bill
						.getString("userPrimaryEmail"));
				statement.setAccountNumber(bill.getString("accountNumber"));
				statement.setPeriodStartDate(bill.getString("periodStartDate"));
				
				statement.setStrCreatedDate(bill.getString("strCreatedDate"));
				
				String documentCoverPathUrl = bill
						.getString("documentCoverPath");
				statement.setDocumentCoverPath(documentCoverPathUrl);

				statement.setCategory(bill.getString("category"));
				
//				if(!ImageUtil.isFileExists(activity, documentCoverPathUrl)){
					arrImagePath.add(documentCoverPathUrl);
//				}
				

				Image displayImagePathBig = new Image();
				Image displayImagePathSmall = new Image();
				Image entityPreviewImagePath = new Image();

				if (bill.getJSONObject("displayImagePathBig") != null) {
					displayImagePathBig.setPath(bill.getJSONObject(
							"displayImagePathBig").getString("path"));
					displayImagePathBig.setAltText(bill.getJSONObject(
							"displayImagePathBig").getString("altText"));
					statement.setDisplayImagePathBig(displayImagePathBig);
					// ImageUtil.downloadImageIfNotExists(activity,
					// displayImagePathBig.getPath());
				}

				if (bill.getJSONObject("displayImagePathSmall") != null) {
					displayImagePathSmall.setPath(bill.getJSONObject(
							"displayImagePathSmall").getString("path"));
					displayImagePathSmall.setAltText(bill.getJSONObject(
							"displayImagePathSmall").getString("altText"));
					statement.setDisplayImagePathSmall(displayImagePathSmall);
					ImageUtil.downloadImageIfNotExists(activity,
							displayImagePathSmall.getPath());
				}

				if (bill.getJSONObject("entityPreviewImagePath") != null) {
					entityPreviewImagePath.setPath(bill.getJSONObject(
							"entityPreviewImagePath").getString("path"));
					entityPreviewImagePath.setAltText(bill.getJSONObject(
							"entityPreviewImagePath").getString("altText"));
					statement.setEntityPreviewImagePath(entityPreviewImagePath);
					// ImageUtil.downloadImageIfNotExists(activity,
					// entityPreviewImagePath.getPath());
				}

				// Level three statements (Bill -> Statement -> SubAccount*)

				// Get SubAccount details.
				JSONArray subAccountJSONArray = null;

				if (bill.get("subAccount") != JSONObject.NULL) {
					subAccountJSONArray = bill.getJSONArray("subAccount");
				}

				if (subAccountJSONArray != null) {
					for (int j = 0; j < subAccountJSONArray.length(); j++) {
						JSONObject subAccountJSON = subAccountJSONArray
								.getJSONObject(j);
						if (subAccountJSON != JSONObject.NULL) {
							SubAccount subAccount = new SubAccount();
							subAccount.setSubAccountName(subAccountJSON
									.getString("subAccountName"));
							subAccount.setSubAccountType(subAccountJSON
									.getString("subAccountType"));
							subAccount.setEndBalance(subAccountJSON
									.getDouble("endBalance"));
							subAccount.setSubAccountNumber(subAccountJSON
									.getString("subAccountNumber"));
							subAccount.setSubAccountId(subAccountJSON
									.getLong("summaryId"));
							subAccount.setItemChecked(subAccountJSON.getBoolean("itemChecked"));
							subAccount.setCategory(subAccountJSON.getString("category"));
							subAccountsList.add(subAccount);
						}
					}
					statement.setSubAccount(subAccountsList);
				}

							JSONArray telephoneStatementsJSONArray = null;

				if (bill.get("telephoneStatements") != JSONObject.NULL) {
					telephoneStatementsJSONArray = bill
							.getJSONArray("telephoneStatements");
				}

				if (telephoneStatementsJSONArray != null) {
					for (int j = 0; j < telephoneStatementsJSONArray.length(); j++) {
						JSONObject telephoneStatementJSON = telephoneStatementsJSONArray
								.getJSONObject(j);

						if (telephoneStatementJSON != JSONObject.NULL) {
							telephoneStatement
									.setTelephoneStatementid(telephoneStatementJSON
											.getLong("telephoneStatementid"));
							telephoneStatement
									.setPlanName(telephoneStatementJSON
											.getString("planName"));

							// Level four statements (Bill -> Statement ->
							// TelephoneStatement -> AccountPlan*)
							List<String> telephoneStatementsAccountPlanList = new ArrayList<String>();

							JSONArray telephoneStatementAccountPlanJSONArray = null;

							if (telephoneStatementJSON.get("accountPlan") != JSONObject.NULL) {
								telephoneStatementAccountPlanJSONArray = telephoneStatementJSON
										.getJSONArray("accountPlan");
							}

							if (telephoneStatementAccountPlanJSONArray != null) {
								for (int k = 0; k < telephoneStatementAccountPlanJSONArray
										.length(); k++) {
									telephoneStatementsAccountPlanList
											.add(telephoneStatementAccountPlanJSONArray
													.getString(k));
								}
								telephoneStatement
										.setAccountPlan(telephoneStatementsAccountPlanList);
							}// if

							telephoneStatementsList.add(telephoneStatement);
						} // if
					}// for
					statement.setTelephoneStatements(telephoneStatementsList);
				}// if

				statementsList.add(statement);
			}// if(bill!=null)

		} // For i
		return statementsList;
	}

	public static void readUnreadStatements(Activity currentActivity,
			long loginUserId, long statementId, boolean isRead)
			throws Exception {
		try {
			HttpClient client = new DefaultHttpClient();
			String webServiceUrl = currentActivity.getResources().getString(
					R.string.webservice_url);

			String statementWebServiceUrl = webServiceUrl
					+ "/user/MarkDocReadOrUnread?userId=" + loginUserId;
			HttpPost post = new HttpPost(statementWebServiceUrl);

			String returnMsg = "";
			ArrayList<MarkReadOrUnRead> markReadOrUnReadList = new ArrayList<MarkReadOrUnRead>();
			MarkReadOrUnRead markReadOrUnRead = new MarkReadOrUnRead();
			markReadOrUnRead.setIsRead(isRead);
			markReadOrUnRead.setId(statementId);

			markReadOrUnReadList.add(markReadOrUnRead);

			Gson gson = new Gson();

			StringEntity input = new StringEntity(
					gson.toJson(markReadOrUnReadList));
			input.setContentType("application/json");

			post.setEntity(input);

			HttpResponse response = client.execute(post);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			returnMsg = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void documentDelete(Activity currentActivity,
			long documentId, boolean isDeleted, long userId) throws Exception {
		try {
			HttpClient client = new DefaultHttpClient();
			String webServiceUrl = currentActivity.getResources().getString(
					R.string.webservice_url);

			String doucumentWebServiceUrl = webServiceUrl
					+ "/user/MarkDocAsDeletedOrUndeleted?userId="+userId;
			HttpPost post = new HttpPost(doucumentWebServiceUrl);

			String returnMsg = "";
			ArrayList<MarkDocumentDelete> markDocumentDeleteList = new ArrayList<MarkDocumentDelete>();
			MarkDocumentDelete markDocumentDelete = new MarkDocumentDelete();
			markDocumentDelete.setIsDeleted(isDeleted);
			markDocumentDelete.setId(documentId);

			markDocumentDeleteList.add(markDocumentDelete);

			Gson gson = new Gson();

			StringEntity input = new StringEntity(
					gson.toJson(markDocumentDeleteList));
			input.setContentType("application/json");

			post.setEntity(input);

			HttpResponse response = client.execute(post);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			returnMsg = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void syncStatementsToBackend(Context currentActivity,
			long loginUserId) throws Exception {

		List<Long> statementIdsList = new ArrayList<Long>();
		List<Statement> statementsListFromDB = new StatementDAO()
				.getAllDirtyStatements(currentActivity, loginUserId);
		
		
		List<Statement> cashFlowDirtyStatementsListFromDB = new StatementDAO().getAllCashFlowDirtyStatements(currentActivity, loginUserId);
		
		if(statementsListFromDB!=null && cashFlowDirtyStatementsListFromDB!=null
				&& (statementsListFromDB.size()>0 || cashFlowDirtyStatementsListFromDB.size()>0) ){
			String statementIds = "";
			for (Statement statement : statementsListFromDB) {
				if(!statementIdsList.contains(statement.getId())){
					statementIds = statementIds + statement.getId() + ",";
					statementIdsList.add(statement.getId());
				}
			}

			for (Statement statement : cashFlowDirtyStatementsListFromDB) {
				if(!statementIdsList.contains(statement.getId())){
					statementIds = statementIds + statement.getId() + ",";
					statementIdsList.add(statement.getId());
				}
			}
			
			if (statementIds.length() > 0) {
				statementIds = statementIds.substring(0,
						statementIds.lastIndexOf(","));
			}

			boolean isSuccessfulReadOrUnread = false;
			boolean isSuccessfulDocDelete = false;
			boolean isSuccessfulPaidOrUnpaid = false;
			boolean isSuccessfulCashFlowItemChecked = false;
			
			isSuccessfulReadOrUnread = updateMarkUserStatementReadOrUnreadToBackend(statementsListFromDB,
					currentActivity, loginUserId);
			isSuccessfulDocDelete = updateMarkUserDocumentDeleteToBackend(statementsListFromDB,
					currentActivity, loginUserId);
			isSuccessfulPaidOrUnpaid = updateMarkPaidUnpaidToBackend(statementsListFromDB, currentActivity,
					loginUserId);
			
			isSuccessfulCashFlowItemChecked = updateCashflowItemsCheckedStatusToBackend(cashFlowDirtyStatementsListFromDB, currentActivity,
					loginUserId);
			// This will revert isDirty=0 for the backed updated offers.
			
			if(isSuccessfulReadOrUnread
				&& isSuccessfulDocDelete
				&& isSuccessfulPaidOrUnpaid
				&& isSuccessfulCashFlowItemChecked){
				StatementDAO.resetDirtyFlagToFalse(statementIds, currentActivity,
						loginUserId);
			}
		}
	}

	private static boolean updateMarkUserDocumentDeleteToBackend(
			List<Statement> statementsListFromDB, Context currentActivity,
			long loginUserId) throws Exception {
		
		boolean isSuccessfulDocDelete = false;
		
		try {

			List<MarkDocumentDelete> markDocumentDeleteList = new ArrayList<MarkDocumentDelete>();

			for (Statement statement : statementsListFromDB) {

				// Mark Document delete/undelete
				MarkDocumentDelete markDocumentDelete = new MarkDocumentDelete();
				markDocumentDelete.setIsDeleted(statement.isPDFDeleted());
				markDocumentDelete.setId(statement.getId());
				markDocumentDeleteList.add(markDocumentDelete);
				markDocumentDelete = null;

			}

			// Webservice to call MarkDocAsDeletedOrUndeleted - START
			HttpClient client = new DefaultHttpClient();
			String webServiceUrl = currentActivity.getResources().getString(
					R.string.webservice_url);

			String doucumentWebServiceUrl = webServiceUrl
					+ "/user/MarkDocAsDeletedOrUndeleted?userId="+loginUserId;
			HttpPost documentPost = new HttpPost(doucumentWebServiceUrl);

			String returnMsg = "";

			Gson gson = new Gson();

			StringEntity input = new StringEntity(
					gson.toJson(markDocumentDeleteList));
			input.setContentType("application/json");

			documentPost.setEntity(input);

			HttpResponse response = client.execute(documentPost);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			returnMsg = sb.toString();
			
			if(response.getStatusLine().getStatusCode()==200){
				isSuccessfulDocDelete = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return isSuccessfulDocDelete;
	}

	private static boolean updateMarkUserStatementReadOrUnreadToBackend(
			List<Statement> statementsListFromDB, Context currentActivity,
			long loginUserId) throws Exception {

		boolean isSuccessfulReadOrUnread = false;
		
		try {
			List<MarkReadOrUnRead> markReadOrUnReadList = new ArrayList<MarkReadOrUnRead>();

			for (Statement statement : statementsListFromDB) {

				// Mark Document read/unread
				MarkReadOrUnRead markReadOrUnRead = new MarkReadOrUnRead();
				markReadOrUnRead.setIsRead(statement.isRead());
				markReadOrUnRead.setId(statement.getId());

				markReadOrUnReadList.add(markReadOrUnRead);
				markReadOrUnRead = null;
			}

			// Webservice to call MarkDocReadOrUnread - START
			HttpClient client = new DefaultHttpClient();
			String webServiceUrl = currentActivity.getResources().getString(
					R.string.webservice_url);

			String markDocReadOrUnreadWebServiceUrl = webServiceUrl
					+ "/user/MarkDocReadOrUnread?userId=" + loginUserId;
			HttpPost post = new HttpPost(markDocReadOrUnreadWebServiceUrl);

			String returnMsg = "";

			Gson gson = new Gson();

			StringEntity input = new StringEntity(
					gson.toJson(markReadOrUnReadList));
			input.setContentType("application/json");

			post.setEntity(input);

			HttpResponse response = client.execute(post);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			returnMsg = sb.toString();
			// Webservice to call MarkDocReadOrUnread - END

			if(response.getStatusLine().getStatusCode()==200){
				isSuccessfulReadOrUnread = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isSuccessfulReadOrUnread;
	}

	private static boolean updateMarkPaidUnpaidToBackend(
			List<Statement> statementsListFromDB, Context currentActivity,
			long loginUserId) throws Exception {

		boolean isSuccessfulPaidOrUnpaid = false;
		try {
			ArrayList<MarkPaidOrUnpaid> markPaidOrUnpaidList = new ArrayList<MarkPaidOrUnpaid>();

			for (Statement statement : statementsListFromDB) {
				MarkPaidOrUnpaid markPaidOrUnpaid = new MarkPaidOrUnpaid();
				markPaidOrUnpaid.setIsPaid(statement.isPaid());
				markPaidOrUnpaid.setId(statement.getId());

				markPaidOrUnpaidList.add(markPaidOrUnpaid);
				markPaidOrUnpaid = null;
			}

			// Webservice to call MarkDocReadOrUnread - START
			HttpClient client = new DefaultHttpClient();
			String webServiceUrl = currentActivity.getResources().getString(
					R.string.webservice_url);

			String markDocReadOrUnreadWebServiceUrl = webServiceUrl
					+ "/user/MarkDocPaidOrUnPaid?userId=" + loginUserId;
			HttpPost post = new HttpPost(markDocReadOrUnreadWebServiceUrl);

			String returnMsg = "";

			Gson gson = new Gson();

			StringEntity input = new StringEntity(
					gson.toJson(markPaidOrUnpaidList));
			input.setContentType("application/json");

			post.setEntity(input);

			HttpResponse response = client.execute(post);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			returnMsg = sb.toString();
			// Webservice to call MarkDocReadOrUnread - END
			
			if(response.getStatusLine().getStatusCode()==200){
				isSuccessfulPaidOrUnpaid = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return isSuccessfulPaidOrUnpaid;
	}

	/*
	 * private static void
	 * updateMarkUserStatementReadOrUnreadAndDocumentDeleteAndPaidUnpaidToBackend
	 * (Context currentActivity, long loginUserId) throws Exception{
	 * 
	 * try{
	 * 
	 * List<Statement> statementsListFromDB = new
	 * StatementDAO().getAllDirtyStatements(currentActivity,loginUserId);
	 * 
	 * List<MarkReadOrUnRead> markReadOrUnReadList = new
	 * ArrayList<MarkReadOrUnRead>(); List<MarkDocumentDelete>
	 * markDocumentDeleteList = new ArrayList<MarkDocumentDelete>();
	 * 
	 * String statementIds = ""; for(Statement statement: statementsListFromDB){
	 * 
	 * //Mark Document read/unread MarkReadOrUnRead markReadOrUnRead = new
	 * MarkReadOrUnRead(); markReadOrUnRead.setIsRead(statement.isRead());
	 * markReadOrUnRead.setId(statement.getId());
	 * 
	 * markReadOrUnReadList.add(markReadOrUnRead); markReadOrUnRead = null;
	 * 
	 * 
	 * //Mark Document delete/undelete MarkDocumentDelete markDocumentDelete =
	 * new MarkDocumentDelete();
	 * markDocumentDelete.setIsDeleted(statement.isPDFDeleted());
	 * markDocumentDelete.setId(statement.getId());
	 * markDocumentDeleteList.add(markDocumentDelete); markDocumentDelete =
	 * null;
	 * 
	 * statementIds=statementIds+statement.getId()+","; }
	 * 
	 * 
	 * if(statementIds.length()>0){
	 * statementIds=statementIds.substring(0,statementIds.lastIndexOf(",")); }
	 * 
	 * 
	 * //Webservice to call MarkDocReadOrUnread - START HttpClient client = new
	 * DefaultHttpClient(); String webServiceUrl =
	 * currentActivity.getResources().getString(R.string.webservice_url);
	 * 
	 * String markDocReadOrUnreadWebServiceUrl = webServiceUrl +
	 * "/user/MarkDocReadOrUnread?userId="+loginUserId; HttpPost post = new
	 * HttpPost(markDocReadOrUnreadWebServiceUrl);
	 * 
	 * String returnMsg="";
	 * 
	 * Gson gson = new Gson();
	 * 
	 * StringEntity input = new StringEntity(gson.toJson(markReadOrUnReadList));
	 * input.setContentType("application/json");
	 * 
	 * post.setEntity(input);
	 * 
	 * HttpResponse response = client.execute(post);
	 * 
	 * BufferedReader rd = new BufferedReader(new
	 * InputStreamReader(response.getEntity().getContent())); StringBuilder sb =
	 * new StringBuilder(); String line = ""; while ((line = rd.readLine()) !=
	 * null) { sb.append(line); }
	 * 
	 * returnMsg = sb.toString(); //Webservice to call MarkDocReadOrUnread - END
	 * 
	 * 
	 * //Webservice to call MarkDocAsDeletedOrUndeleted - START //HttpClient
	 * client = new DefaultHttpClient(); //String webServiceUrl =
	 * currentActivity.getResources().getString(R.string.webservice_url);
	 * 
	 * String doucumentWebServiceUrl = webServiceUrl +
	 * "/user/MarkDocAsDeletedOrUndeleted"; HttpPost documentPost = new
	 * HttpPost(doucumentWebServiceUrl);
	 * 
	 * //String returnMsg=""; returnMsg="";
	 * 
	 * 
	 * //Gson gson = new Gson();
	 * 
	 * //StringEntity input = new
	 * StringEntity(gson.toJson(markDocumentDeleteList));
	 * input.setContentType("application/json");
	 * 
	 * documentPost.setEntity(input);
	 * 
	 * response = client.execute(documentPost);
	 * 
	 * BufferedReader rd1 = new BufferedReader(new
	 * InputStreamReader(response.getEntity().getContent())); StringBuilder sb1
	 * = new StringBuilder(); String line1 = ""; while ((line1 = rd1.readLine())
	 * != null) { sb1.append(line1); }
	 * 
	 * returnMsg = sb1.toString(); //Webservice to call
	 * MarkDocAsDeletedOrUndeleted - END
	 * 
	 * //This will revert isDirty=0 for the backed updated offers.
	 * resetDirtyFlagToFalse(statementIds, currentActivity, loginUserId);
	 * 
	 * return;
	 * 
	 * }catch(Exception e){ e.printStackTrace(); } }
	 */

	public static void paidUnpaidStatements(Activity currentActivity, long id,
			boolean isPaid, long loginUserId) throws Exception {
		try {
			HttpClient client = new DefaultHttpClient();
			String webServiceUrl = currentActivity.getResources().getString(
					R.string.webservice_url);

			String statementWebServiceUrl = webServiceUrl
					+ "/user/MarkDocPaidOrUnPaid?userId=" + loginUserId;
			HttpPost post = new HttpPost(statementWebServiceUrl);

			String returnMsg = "";
			ArrayList<MarkPaidOrUnpaid> markPaidOrUnpaidList = new ArrayList<MarkPaidOrUnpaid>();
			MarkPaidOrUnpaid markPaidOrUnpaid = new MarkPaidOrUnpaid();
			markPaidOrUnpaid.setIsPaid(isPaid);
			markPaidOrUnpaid.setId(id);

			markPaidOrUnpaidList.add(markPaidOrUnpaid);

			Gson gson = new Gson();

			StringEntity input = new StringEntity(
					gson.toJson(markPaidOrUnpaidList));
			input.setContentType("application/json");

			post.setEntity(input);

			HttpResponse response = client.execute(post);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			returnMsg = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void updateCashflowItemCheckedStatusToBackend(Activity currentActivity, long id,
			boolean itemChecked, long loginUserId) throws Exception {
		try {
			HttpClient client = new DefaultHttpClient();
			String webServiceUrl = currentActivity.getResources().getString(
					R.string.webservice_url);

			String statementWebServiceUrl = webServiceUrl
					+ "/user/updateCashflowChecked?userId=" + loginUserId;
			
			HttpPost post = new HttpPost(statementWebServiceUrl);

			String returnMsg = "";
			ArrayList<MarkItemChecked> markItemCheckedList = new ArrayList<MarkItemChecked>();
			
			MarkItemChecked markItemChecked = new MarkItemChecked();
			markItemChecked.setItemChecked(itemChecked);
			markItemChecked.setId(id);

			markItemCheckedList.add(markItemChecked);

			Gson gson = new Gson();

			StringEntity input = new StringEntity(
					gson.toJson(markItemCheckedList));
			input.setContentType("application/json");

			post.setEntity(input);

			HttpResponse response = client.execute(post);

			//String returnMsg = "";
			//HttpResponse response = client.execute(post);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			returnMsg = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void updateBillorStatementAssistanceInfo(String url, Activity currentActivity,
			long loginUserId, long statmentId, boolean isChecked, String sendColumntoBackend)
			throws Exception {
		try {
			HttpClient client = new DefaultHttpClient();
			String webServiceUrl = currentActivity.getResources().getString(R.string.webservice_url);

			String statementWebServiceUrl = webServiceUrl + url + "?userId=" + loginUserId;
			HttpPost post = new HttpPost(statementWebServiceUrl);

			String returnMsg = "";
			ArrayList<MarkReadOrUnRead> markReadOrUnReadList = new ArrayList<MarkReadOrUnRead>();
			MarkReadOrUnRead markReadOrUnRead = new MarkReadOrUnRead();
			if("docInBill".equalsIgnoreCase(sendColumntoBackend)) {
				markReadOrUnRead.setDocInBill(isChecked);
			} else if("docInCashflow".equalsIgnoreCase(sendColumntoBackend)) {
				markReadOrUnRead.setDocInCashflow(isChecked);
			} else if("docInStatement".equalsIgnoreCase(sendColumntoBackend)) {
				markReadOrUnRead.setDocInStatement(isChecked);
			} else if("docInUnPaid".equalsIgnoreCase(sendColumntoBackend)) {
				markReadOrUnRead.setDocInUnPaid(isChecked);
			}
			
			markReadOrUnRead.setId(statmentId);

			markReadOrUnReadList.add(markReadOrUnRead);

			Gson gson = new Gson();

			StringEntity input = new StringEntity(
					gson.toJson(markReadOrUnReadList));
			input.setContentType("application/json");

			post.setEntity(input);

			HttpResponse response = client.execute(post);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			returnMsg = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void updateFavourite(Context currentActivity,long loginUserId,long providerId,boolean isFavourite) throws Exception {
		
		try {
			HttpClient client = new DefaultHttpClient();
			String webServiceUrl = currentActivity.getResources().getString(
					R.string.webservice_url);

			String documentWebServiceUrl = webServiceUrl
					+ "/user/updateUserFavorites?userId=" + loginUserId;
			HttpPost post = new HttpPost(documentWebServiceUrl);

			Boolean isSuccess = false;
			ArrayList<UserFavorite> userFavouriteList = new ArrayList<UserFavorite>();
			UserFavorite userFavorite = new UserFavorite();
			userFavorite.setProviderId(providerId);
			userFavorite.setFavorite(true);
			OfferProvidersDAO offerProvidersDAO = new OfferProvidersDAO();
			boolean isOffer = offerProvidersDAO.isFavourit(loginUserId, (int)providerId, currentActivity);
			userFavorite.setIsOffer(isOffer);
			userFavorite.setIsDocument(isFavourite);
			userFavouriteList.add(userFavorite);

			Gson gson = new Gson();

			StringEntity input = new StringEntity(gson.toJson(userFavouriteList));
			input.setContentType("application/json");

			post.setEntity(input);

			HttpResponse response = client.execute(post);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			isSuccess = Boolean.valueOf(sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void syncFavoritesToBackend(Context currentActivity,long loginUserId) throws Exception {
		List<DocumentProviders> documentProvidersList = new DocumentProvidersDAO().getAllDirtyFavorites(currentActivity, loginUserId);

		List<UserFavorite> userFavoriteList = new ArrayList<UserFavorite>();
		
		String providerIds = "";

		for (DocumentProviders documentProviders : documentProvidersList) {
			UserFavorite userFavorite = new UserFavorite();
			userFavorite.setProviderId(documentProviders.getProviderId());
			userFavorite.setFavorite(documentProviders.isFavorite());

			userFavoriteList.add(userFavorite);
			userFavorite = null;

			providerIds = providerIds + documentProviders.getProviderId() + ",";
		}

		if (providerIds.length() > 0) {
			providerIds = providerIds.substring(0, providerIds.lastIndexOf(","));
			
			updateDocumentFavorites(documentProvidersList,currentActivity, loginUserId);
			DocumentProvidersDAO.resetDirtyFavoritesToFalse(providerIds, currentActivity, loginUserId);
		}
	}
	
	public static void updateDocumentFavorites(List<DocumentProviders> documentProvidersList, Context currentActivity,long loginUserId) throws Exception {
		try {
			HttpClient client = new DefaultHttpClient();
			String webServiceUrl = currentActivity.getResources().getString(
					R.string.webservice_url);

			String documentWebServiceUrl = webServiceUrl
					+ "/user/updateUserFavorites?userId=" + loginUserId;
			HttpPost post = new HttpPost(documentWebServiceUrl);

			Boolean isSuccess = false;
			ArrayList<UserFavorite> userFavoriteList = new ArrayList<UserFavorite>();
			
			OfferProvidersDAO offerProvidersDAO = new OfferProvidersDAO();
			for (DocumentProviders documentProviders : documentProvidersList) {
				
				UserFavorite userFavorite = new UserFavorite();
				userFavorite.setProviderId(documentProviders.getProviderId());
				userFavorite.setFavorite(true);
				boolean isOffer = offerProvidersDAO.isFavourit(loginUserId, (int)documentProviders.getProviderId(), currentActivity);
				userFavorite.setIsOffer(isOffer);
				userFavorite.setIsDocument(documentProviders.isFavorite());
				userFavoriteList.add(userFavorite);
				userFavorite = null;

			}

			Gson gson = new Gson();

			StringEntity input = new StringEntity(gson.toJson(userFavoriteList));
			input.setContentType("application/json");

			post.setEntity(input);

			HttpResponse response = client.execute(post);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			isSuccess = Boolean.valueOf(sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean updateCashflowItemsCheckedStatusToBackend(
			List<Statement> statementsListFromDB, Context currentActivity,
			long loginUserId) throws Exception {

		boolean isSuccessfulCashFlowItemChecked = false;
		try {
			ArrayList<MarkItemChecked> markItemCheckedList = new ArrayList<MarkItemChecked>();

			for (Statement statement : statementsListFromDB) {
				MarkItemChecked markItemChecked = new MarkItemChecked();
				markItemChecked.setItemChecked(statement.isItemChecked());
				markItemChecked.setId(statement.getSubAccountId());

				markItemCheckedList.add(markItemChecked);
				markItemChecked = null;
			}

			// Webservice to call MarkDocReadOrUnread - START
			HttpClient client = new DefaultHttpClient();
			String webServiceUrl = currentActivity.getResources().getString(
					R.string.webservice_url);

			String statementWebServiceUrl = webServiceUrl
					+ "/user/updateCashflowChecked?userId=" + loginUserId;
			
			HttpPost post = new HttpPost(statementWebServiceUrl);

			String returnMsg = "";
			
			Gson gson = new Gson();

			StringEntity input = new StringEntity(
					gson.toJson(markItemCheckedList));
			input.setContentType("application/json");

			post.setEntity(input);

			HttpResponse response = client.execute(post);

			//String returnMsg = "";
			//HttpResponse response = client.execute(post);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			returnMsg = sb.toString();
			
			if(response.getStatusLine().getStatusCode()==200){
				isSuccessfulCashFlowItemChecked = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return isSuccessfulCashFlowItemChecked;
	}

	public static void synStatementCategory(Context activity) {
		
		HttpClient client = new DefaultHttpClient();
		String webServiceUrl = activity.getResources().getString(R.string.webservice_url);

		String userBillsCategoryWebServiceUrl = webServiceUrl + "/getBillCategories";
		String userCashFlowCategoryWebServiceUrl = webServiceUrl + "/getCashflowCategories";
		// System.out.println("LastSyncDateTime:"+lastSyncDateTime);
		/*if (lastSyncDateTime != 0l) {
			offerWebServiceUrl += "&date=" + lastSyncDateTime;
		}*/

		
		try {
			HttpGet request = new HttpGet(userBillsCategoryWebServiceUrl);
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuilder responseJSONText = new StringBuilder();

			String line = "";
			while ((line = rd.readLine()) != null) {
				responseJSONText.append(line);
			}
			
			JSONArray billsCategroyArray = new JSONArray(responseJSONText.toString());
			for (int i = 0; i < billsCategroyArray.length(); i++) {
				JSONObject JSON = billsCategroyArray.getJSONObject(i);
				String strAccountTypeId = JSON.getString("accountTypeId");
				String strAccountType = JSON.getString("accountType");
				String strDisplayName = JSON.getString("displayName");
				
				Log.d("Insert: ", "Inserting Bills Categories..");
				
				
				StatementDAO.addCashFlowCategories(activity, strAccountTypeId, strAccountType,  strDisplayName, "Bill");
				
				Log.d("Insert: ", "Inserting Bills Categories Completed");
			}
			
			
			HttpGet requestCashFlowCategory = new HttpGet(userCashFlowCategoryWebServiceUrl);
			HttpResponse responseCashFlowCategory = client.execute(requestCashFlowCategory);
			BufferedReader rdCashFlowCategory = new BufferedReader(new InputStreamReader(responseCashFlowCategory.getEntity().getContent()));
			StringBuilder responseCashFlowCategoryJSONText = new StringBuilder();

			String line1 = "";
			while ((line1 = rdCashFlowCategory.readLine()) != null) {
				responseCashFlowCategoryJSONText.append(line1);
			}
			
			JSONArray cashFlowCategoryArray = new JSONArray(responseCashFlowCategoryJSONText.toString());
			for (int i = 0; i < cashFlowCategoryArray.length(); i++) {
				JSONObject JSONCashFlow = cashFlowCategoryArray.getJSONObject(i);
				String strAccountTypeId = JSONCashFlow.getString("accountTypeId");
				String strAccountType = JSONCashFlow.getString("accountType");
				String strDisplayName = JSONCashFlow.getString("displayName");
				
				Log.d("Insert: ", "Inserting CashFlow Categories..");
				
				
				StatementDAO.addCashFlowCategories(activity, strAccountTypeId, strAccountType,  strDisplayName, "CashFlow");
				
				Log.d("Insert: ", "Inserting CashFlow Categories Completed..");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
