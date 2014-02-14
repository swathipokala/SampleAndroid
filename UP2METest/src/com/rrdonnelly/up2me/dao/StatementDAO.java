package com.rrdonnelly.up2me.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.rrdonnelly.up2me.json.Image;
import com.rrdonnelly.up2me.json.OfferProviders;
import com.rrdonnelly.up2me.json.Statement;
import com.rrdonnelly.up2me.json.StatementCategory;
import com.rrdonnelly.up2me.json.SubAccount;
import com.rrdonnelly.up2me.json.TelephoneStatement;
import com.rrdonnelly.up2me.util.ImageUtil;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class StatementDAO {

	private static final String TABLE_STATEMENTS = "statements";
	private static final String TABLE_STATEMENTS_SUB_ACCOUNTS = "StatementsSubAccounts";
	// private static final String TABLE_DOC_TRANSACTION = "DocTransaction1";

	private static final String TABLE_TELPHONE_STATEMENT = "telphonestatement";
	private static final String TABLE_TELEPHONE_STATEMENT_ACCOUNT_PLAN = "telephonestatementaccountplan";
	
	private static final String TABLE_STATEMENTS_CATEGORY = "StatementsCategory";

	public void refresh(Activity activity) {
		deleteStatements(activity);
		deleteSubAccounts(activity);
		// deleteDocTransaction(activity);
		deleteTelephoneStatements(activity);
		deleteTelephoneStatementAccountPlans(activity);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	public static void addStatement(Statement billStatement, long loginUserId,
			Context activity) {
		Long statementId = 0l;
		Long id = 0l;
		String name = "";
		String displayImagePathBig = null;
		String displayImagePathBigAltText = null;
		String displayImagePathSmall = null;
		String dispalyImagePathSmallAltText = null;
		String documentPath = "";
		String entityPreviewImagePath = null;
		String message = null;
		String messageType = null;
		String statementDisplayDate = null;
		long subAccount = 0;
		String periodEndDate = null;
		String dueDate = null;
		double minAmountDue = 0.0;
		double balance = 0.0;
		String providerName = "";
		boolean valid = false;
		int telephoneStatements = 0;
		boolean read = false;
		boolean paid = false;
		boolean bill = false;
		boolean delete = false;

		// New web service change.
		long providerId = 0;
		String userPrimaryEmail = "";
		String accountNumber = "";
		String periodStartDate = "";
		double newBalance = 0.0;
		double minPayment = 0.0;

		double amountEnclosed = 0.0;
		long cardTypeId = 0;
		String cardTypeName = "";
		String cardNumber = "";
		String cardHoldername = "";
		String validThrough = "";
		String invoiceNumber = "";
		long numberOfLines = 0;
		String docData = "";
		long createdDate = 0;
		String checksum = "";
		String accountType = "";
		String strCreatedDate = "";
		double paymentsCredits = 0.0;
		String paymentDate = "";
		double monthlyCharges = 0.0;
		double usageAndPurchaseCharges = 0.0;
		double surAndOtherChargesCredits = 0.0;
		double taxesAndFees = 0.0;
		double totalAccountChargesAndCredits = 0.0;
		double newPurchases = 0.0;
		double newCashAdvance = 0.0;
		double fees = 0.0;
		double financeCharges = 0.0;
		double creditLineCash = 0.0;
		double creditLineTotal = 0.0;
		double availableCreditCash = 0.0;
		double availableCreditTotal = 0.0;
		String latePaymentWarning = "";
		String minPaymentWarning = "";
		double currentMinAmountDue = 0.0;
		double pastDue = 0.0;
		double overlimitAmount = 0.0;
		double avgDailyBalPurchases = 0.0;
		double avgDailyBalCashAdv = 0.0;
		double nominalAnnualPerRatePurchases = 0.0;
		double nominalAnnualPerRateCashAdv = 0.0;
		double monPeriodRatePurchases = 0.0;
		double monPeriodRateCashAdv = 0.0;
		double annualPerRatePurchases = 0.0;
		double annualPerRateCashAdv = 0.0;
		String documentCoverPath = "";
		String account = "";
		
		boolean docInBill = false;
		boolean docInUnPaid = false;
		boolean docInCashflow = false;
		boolean docInStatement = false;

		id = billStatement.getId();
		name = billStatement.getName();
		displayImagePathBig = billStatement.getDisplayImagePathBig().getPath();
		displayImagePathBigAltText = billStatement.getDisplayImagePathBig()
				.getAltText();
		displayImagePathSmall = billStatement.getDisplayImagePathSmall()
				.getPath();
		dispalyImagePathSmallAltText = billStatement.getDisplayImagePathSmall()
				.getAltText();

		documentPath = billStatement.getDocumentPath();
		entityPreviewImagePath = billStatement.getEntityPreviewImagePath()
				.getPath();
		message = billStatement.getMessage();
		messageType = billStatement.getMessageType();
		statementDisplayDate = billStatement.getStatementDisplayDate();
		subAccount = 0; // TO DO: Sub Account does not having columns in
						// Statement table.
		periodEndDate = billStatement.getPeriodEndDate();
		dueDate = billStatement.getDueDate();
		minAmountDue = billStatement.getMinAmountDue();
		balance = billStatement.getBalance();
		providerName = billStatement.getProviderName();
		valid = billStatement.isValid();
		telephoneStatements = 0; // TO DO: Telephone Statements does not having
									// columns in Statement table.
		read = billStatement.isRead();
		paid = billStatement.isPaid();
		bill = billStatement.isBill();
		
		docInBill = billStatement.isDocInBill();
		docInUnPaid = billStatement.isDocInUnPaid();
		docInCashflow = billStatement.isDocInCashflow();
		docInStatement = billStatement.isDocInStatement();
				
		// New web service change.
		// providerId = billStatement.getProviderId();
		userPrimaryEmail = billStatement.getUserPrimaryEmail();
		accountNumber = billStatement.getAccountNumber();
		periodStartDate = billStatement.getPeriodStartDate();
		/*
		 * newBalance = billStatement.getNewBalance(); minPayment =
		 * billStatement.getMinPayment(); amountEnclosed =
		 * billStatement.getAmountEnclosed(); cardTypeId =
		 * billStatement.getCardTypeId(); cardTypeName =
		 * billStatement.getCardTypeName(); cardNumber =
		 * billStatement.getCardNumber(); cardHoldername =
		 * billStatement.getCardHoldername(); validThrough =
		 * billStatement.getValidThrough(); invoiceNumber =
		 * billStatement.getInvoiceNumber(); numberOfLines =
		 * billStatement.getNumberOfLines(); docData =
		 * billStatement.getDocData(); createdDate =
		 * billStatement.getCreatedDate(); checksum =
		 * billStatement.getChecksum(); accountType =
		 * billStatement.getAccountType();
		 */
		strCreatedDate = billStatement.getStrCreatedDate();
		/*
		 * paymentsCredits = billStatement.getPaymentsCredits(); paymentDate =
		 * billStatement.getPaymentDate(); monthlyCharges =
		 * billStatement.getMonthlyCharges(); usageAndPurchaseCharges =
		 * billStatement.getUsageAndPurchaseCharges(); surAndOtherChargesCredits
		 * = billStatement.getSurAndOtherChargesCredits(); taxesAndFees =
		 * billStatement.getTaxesAndFees(); totalAccountChargesAndCredits =
		 * billStatement.getTotalAccountChargesAndCredits(); newPurchases =
		 * billStatement.getNewPurchases(); newCashAdvance =
		 * billStatement.getNewCashAdvance(); fees = billStatement.getFees();
		 * financeCharges = billStatement.getFinanceCharges(); creditLineCash =
		 * billStatement.getCreditLineCash(); creditLineTotal =
		 * billStatement.getCreditLineTotal(); availableCreditCash =
		 * billStatement.getAvailableCreditCash(); availableCreditTotal =
		 * billStatement.getAvailableCreditTotal(); latePaymentWarning =
		 * billStatement.getLatePaymentWarning(); minPaymentWarning =
		 * billStatement.getMinPaymentWarning(); currentMinAmountDue =
		 * billStatement.getCurrentMinAmountDue(); pastDue =
		 * billStatement.getPastDue(); overlimitAmount =
		 * billStatement.getOverlimitAmount(); avgDailyBalPurchases =
		 * billStatement.getAvgDailyBalPurchases(); avgDailyBalCashAdv =
		 * billStatement.getAvgDailyBalCashAdv(); nominalAnnualPerRatePurchases
		 * = billStatement.getNominalAnnualPerRatePurchases();
		 * nominalAnnualPerRateCashAdv =
		 * billStatement.getNominalAnnualPerRateCashAdv();
		 * monPeriodRatePurchases = billStatement.getMonPeriodRateCashAdv();
		 * monPeriodRateCashAdv = billStatement.getMonPeriodRateCashAdv();
		 * annualPerRatePurchases = billStatement.getAnnualPerRateCashAdv();
		 * annualPerRateCashAdv = billStatement.getAnnualPerRateCashAdv();
		 */
		documentCoverPath = billStatement.getDocumentCoverPath();
		// account = billStatement.getAccount();
		
		delete = billStatement.isPDFDeleted();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		// values.put("ID", billStatement.getId());
		values.put("Id", billStatement.getId());
		values.put("name", name);

		values.put("displayImagePathBig", displayImagePathBig);
		values.put("displayImagePathBigAltText", displayImagePathBigAltText);

		values.put("displayImagePathSmall", displayImagePathSmall);
		values.put("dispalyImagePathSmallAltText", dispalyImagePathSmallAltText);

		values.put("documentPath", documentPath);
		values.put("entityPreviewImagePath", entityPreviewImagePath);

		values.put("message", message);
		values.put("messageType", messageType);

		values.put("statementDisplayDate", statementDisplayDate);
		// values.put("subAccount", subAccount);

		values.put("periodEndDate", periodEndDate);
		values.put("dueDate", dueDate);

		values.put("minAmountDue", minAmountDue);
		values.put("balance", balance);

		values.put("providerName", providerName);
		values.put("valid", valid);

		// values.put("telephoneStatements", telephoneStatements);
		values.put("read", read);

		values.put("paid", paid);
		values.put("bill", bill);
		
		values.put("showInCashFlow", docInCashflow);
		values.put("showInStatements", docInStatement);
		values.put("showInBill", docInBill);
		values.put("showInUnpaid", docInUnPaid);
		
		// values.put("userID", loginUserId);
		values.put("userId", loginUserId);
		values.put("category", billStatement.getCategory());

		// New web service change.
		// values.put("providerId", providerId);
		values.put("userPrimaryEmail", userPrimaryEmail);
		values.put("accountNumber", accountNumber);
		values.put("periodStartDate", periodStartDate);
		/*
		 * values.put("newBalance", newBalance);
		 * values.put("minPayment",minPayment ); values.put("amountEnclosed",
		 * amountEnclosed); values.put("cardTypeId", cardTypeId);
		 * values.put("cardTypeName", cardTypeName); values.put("cardNumber",
		 * cardNumber); values.put("cardHoldername", cardHoldername );
		 * values.put("validThrough", validThrough); values.put("invoiceNumber",
		 * invoiceNumber); values.put("numberOfLines", numberOfLines);
		 * values.put("docData", docData); values.put("createdDate",
		 * createdDate); values.put("checksum", checksum);
		 * values.put("accountType", accountType);
		 */
		values.put("strCreatedDate", strCreatedDate);
		/*
		 * values.put("paymentsCredits", paymentsCredits);
		 * values.put("paymentDate", paymentDate); values.put("monthlyCharges",
		 * monthlyCharges); values.put("usageAndPurchaseCharges",
		 * usageAndPurchaseCharges); values.put("surAndOtherChargesCredits",
		 * surAndOtherChargesCredits); values.put("taxesAndFees", taxesAndFees);
		 * values.put("totalAccountChargesAndCredits",
		 * totalAccountChargesAndCredits); values.put("newPurchases",
		 * newPurchases); values.put("newCashAdvance", newCashAdvance);
		 * values.put("fees", fees); values.put("financeCharges",
		 * financeCharges); values.put("creditLineCash", creditLineCash);
		 * values.put("creditLineTotal", creditLineTotal);
		 * values.put("availableCreditCash", availableCreditCash);
		 * values.put("availableCreditTotal", availableCreditTotal);
		 * values.put("latePaymentWarning", latePaymentWarning);
		 * values.put("minPaymentWarning", minPaymentWarning);
		 * values.put("currentMinAmountDue", currentMinAmountDue);
		 * values.put("pastDue", pastDue); values.put("overlimitAmount",
		 * overlimitAmount); values.put("avgDailyBalPurchases",
		 * avgDailyBalPurchases); values.put("avgDailyBalCashAdv",
		 * avgDailyBalCashAdv); values.put("nominalAnnualPerRatePurchases",
		 * nominalAnnualPerRatePurchases);
		 * values.put("nominalAnnualPerRateCashAdv",
		 * nominalAnnualPerRateCashAdv); values.put("monPeriodRatePurchases",
		 * monPeriodRatePurchases); values.put("monPeriodRateCashAdv",
		 * monPeriodRateCashAdv); values.put("annualPerRatePurchases",
		 * annualPerRatePurchases); values.put("annualPerRateCashAdv",
		 * annualPerRateCashAdv);
		 */
		values.put("documentCoverPath", documentCoverPath);
		// values.put("account", account);
		values.put("isPDFDeleted",delete);

		// Inserting Row
		long rowId = db.insert(TABLE_STATEMENTS, null, values);

		// db.close(); // Closing database connection

		long stId = getStatementId(rowId, activity);

		System.out.println("Row ID: " + rowId);
		System.out.println("Statement ID: " + stId);

		addSubAccountList(billStatement.getSubAccount(), billStatement.getId(),
				loginUserId, activity);
		// addTransactionsList(billStatement.getTransactions(),
		// billStatement.getId(), loginUserId, activity);
		addTelephoneStatementsList(billStatement.getTelephoneStatements(),
				billStatement.getId(), loginUserId, activity);

	}

	private static void addSubAccountList(List<SubAccount> subAccountsList,
			long statementId, long userId, Context activity) {
		if (subAccountsList != null) {
			for (SubAccount subAccount : subAccountsList) {
				addSubAccount(subAccount, statementId, userId, activity);
			}
		}
	}

	/*
	 * public void addTransactionsList(List<DocTransaction> transactionsList,
	 * long statementId, long userId, Activity activity) {
	 * if(transactionsList!=null){ for(DocTransaction docTransaction :
	 * transactionsList){ addTransaction(docTransaction, statementId, userId,
	 * activity); } } }
	 */

	/*
	 * public void addTransaction(DocTransaction docTransaction, long
	 * statementId, long userId, Activity activity) {
	 * 
	 * 
	 * long transactionId = 0; long docHeaderId = 0; String lineNumber = "";
	 * String transType = ""; String transDate = ""; String time = ""; String
	 * number = ""; String rate = ""; String usageType = ""; String origination
	 * = ""; String destination = ""; int min = 0; double airTimeCharges = 0.0;
	 * double longDistOtherCharges = 0.0; double totalAmount = 0.0; String
	 * dateOfPost = "";
	 * 
	 * String referenceNumber = ""; String merchant = ""; String location = "";
	 * String description = ""; double applicationPrice = 0.0; String
	 * strTransDate = ""; String sicCode = ""; double charge = 0.0; double
	 * credit = 0.0;
	 * 
	 * 
	 * if(docTransaction!=null){ transactionId =
	 * docTransaction.getTransactionId(); docHeaderId =
	 * docTransaction.getDocHeaderId(); lineNumber =
	 * docTransaction.getLineNumber(); transType =
	 * docTransaction.getTransType(); transDate = docTransaction.getTransDate();
	 * time = docTransaction.getTime(); number = docTransaction.getNumber();
	 * rate = docTransaction.getRate(); usageType =
	 * docTransaction.getUsageType(); origination =
	 * docTransaction.getOrigination(); destination =
	 * docTransaction.getDestination(); min = docTransaction.getMin();
	 * airTimeCharges = docTransaction.getAirTimeCharges(); longDistOtherCharges
	 * = docTransaction.getLongDistOtherCharges(); totalAmount =
	 * docTransaction.getTotalAmount(); dateOfPost =
	 * docTransaction.getDateOfPost();
	 * 
	 * referenceNumber = docTransaction.getReferenceNumber(); merchant =
	 * docTransaction.getMerchant(); location = docTransaction.getLocation();
	 * description = docTransaction.getDescription(); applicationPrice =
	 * docTransaction.getApplicationPrice(); strTransDate =
	 * docTransaction.getStrTransDate(); sicCode = docTransaction.getSicCode();
	 * charge = docTransaction.getCharge(); credit = docTransaction.getCredit();
	 * }
	 * 
	 * 
	 * 
	 * DatabaseHandler databaseHandler =
	 * DatabaseHandler.getDatabaseHandlerInstance(activity);
	 * 
	 * SQLiteDatabase db = databaseHandler.getWritableDatabase();
	 * 
	 * ContentValues values = new ContentValues(); values.put("transactionId",
	 * transactionId); values.put("docHeaderId", docHeaderId);
	 * values.put("lineNumber", lineNumber); values.put("transType", transType);
	 * values.put("transDate", transDate); values.put("time", time);
	 * values.put("number", number); values.put("rate", rate);
	 * values.put("usageType", usageType); values.put("origination",
	 * origination); values.put("destination", destination); values.put("min",
	 * min); values.put("airTimeCharges", airTimeCharges);
	 * values.put("longDistOtherCharges", longDistOtherCharges);
	 * values.put("totalAmount", totalAmount); values.put("dateOfPost",
	 * dateOfPost); values.put("referenceNumber", referenceNumber);
	 * values.put("merchant", merchant); values.put("location", location);
	 * values.put("description", description); values.put("applicationPrice",
	 * applicationPrice); values.put("strTransDate", strTransDate);
	 * values.put("sicCode", sicCode); values.put("charge", charge);
	 * values.put("credit", credit);
	 * 
	 * // Inserting Row db.insert(TABLE_DOC_TRANSACTION, null, values);
	 * 
	 * //db.close(); // Closing database connection }
	 */

	private static void addTelephoneStatementsList(
			List<TelephoneStatement> telephoneStatementsList, long statementId,
			long userId, Context activity) {
		if (telephoneStatementsList != null) {

			for (TelephoneStatement telephoneStatement : telephoneStatementsList) {
				addTelephoneStatement(telephoneStatement, statementId, userId,
						activity);
			}

		}
	}

	private static void addTelephoneStatement(
			TelephoneStatement telephoneStatement, long statementId,
			long userId, Context activity) {
		long telephoneStatementId = 0l;
		String planName = "";
		List<String> accountPlan = new ArrayList<String>();

		if (telephoneStatement != null) {
			telephoneStatementId = telephoneStatement.getTelephoneStatementId();
			planName = telephoneStatement.getPlanName();
			accountPlan = telephoneStatement.getAccountPlan();
		}

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("telephoneStatementId", telephoneStatementId);
		values.put("planName", planName);
		values.put("userId", userId);
		values.put("statementId", statementId);

		// Inserting Row
		db.insert(TABLE_TELPHONE_STATEMENT, null, values);

		// db.close(); // Closing database connection

		addTelephoneStatementsAccountPlans(accountPlan, telephoneStatementId,
				statementId, userId, activity);
	}

	private static void addTelephoneStatementsAccountPlans(
			List<String> accountPlans, long telephoneStatementId,
			long statementId, long userId, Context activity) {
		for (String accountPlan : accountPlans) {
			addTelephoneStatementAccountPlan(accountPlan, telephoneStatementId,
					statementId, userId, activity);
		}
	}

	private static void addTelephoneStatementAccountPlan(String accountPlan,
			long telephoneStatementId, long statementId, long userId,
			Context activity) {

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("telephoneStatementId", telephoneStatementId);
		values.put("accountPlan", accountPlan);
		values.put("userId", userId);
		values.put("statementId", statementId);

		// Inserting Row
		db.insert(TABLE_TELEPHONE_STATEMENT_ACCOUNT_PLAN, null, values);

		// db.close(); // Closing database connection
	}

	private static void addSubAccount(SubAccount subAccount, long statementId,
			long userId, Context activity) {

		String subAccountName = "";
		String subAccountType = "";
		double endBalance = 0.0;
		String subAccountNumber = "";
		long subAccountId = 0l;
		boolean itemChecked = false;
		

		if (subAccount != null) {
			subAccountName = subAccount.getSubAccountName();
			subAccountType = subAccount.getSubAccountType();
			endBalance = subAccount.getEndBalance();
			subAccountId = subAccount.getSubAccountId();
			itemChecked = subAccount.isItemChecked();
		}

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("statementId", statementId);
		values.put("subAccountName", subAccountName);
		values.put("subAccountType", subAccountType);
		values.put("endBalance", endBalance);
		values.put("userId", userId);
		values.put("subAccountId", subAccountId);
		values.put("category", subAccount.getCategory());
		values.put("itemChecked", itemChecked);
		

		// Inserting Row
		db.insert(TABLE_STATEMENTS_SUB_ACCOUNTS, null, values);

		// db.close(); // Closing database connection
	}

	public void deleteStatements(Activity activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		db.delete(TABLE_STATEMENTS, null, null);
		// db.close();
	}

	public void deleteSubAccounts(Activity activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		db.delete(TABLE_STATEMENTS_SUB_ACCOUNTS, null, null);
		// db.close();
	}

	/*
	 * public void deleteDocTransaction(Activity activity) { DatabaseHandler
	 * databaseHandler = DatabaseHandler.getDatabaseHandlerInstance(activity);
	 * 
	 * SQLiteDatabase db = databaseHandler.getWritableDatabase();
	 * db.delete(TABLE_DOC_TRANSACTION, null, null); //db.close(); }
	 */

	public void deleteTelephoneStatements(Activity activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		db.delete(TABLE_TELPHONE_STATEMENT, null, null);
		// db.close();
	}

	public void deleteTelephoneStatementAccountPlans(Activity activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		db.delete(TABLE_TELEPHONE_STATEMENT_ACCOUNT_PLAN, null, null);
		// db.close();
	}

	public int updateRead(long id, int read, Activity activity, int isDirty) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("read", read);
		values.put("isDirty", isDirty);

		// updating row
		return db.update(TABLE_STATEMENTS, values, "ID = ?",
				new String[] { String.valueOf(id) });
	}

	public int updateIsPdfDeleted(long id, int deleted, Activity activity,
			int isDirty) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("isPDFDeleted", deleted);
		values.put("isDirty", isDirty);

		// updating row
		return db.update(TABLE_STATEMENTS, values, "ID = ?",
				new String[] { String.valueOf(id) });
	}

	private static long getStatementId(long rowId, Context activity) {
		long statementId = 0l;
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		String countQuery = "SELECT  * FROM " + TABLE_STATEMENTS
				+ " WHERE ROWID = " + rowId;
		SQLiteDatabase db = databaseHandler.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		statementId = Long.parseLong(cursor.getString(0));
		cursor.close();

		// return count
		return statementId;
	}

	public ArrayList<Statement> getAllCashFlowStatements(Activity activity,
			Long userid, String providerIds, String categories) {
		
		ArrayList<Statement> statementList = new ArrayList<Statement>();
		String providerCondition ="";
		String categoriesCondition ="";
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		if(providerIds != null && providerIds.length()>0){
			
			providerCondition = " and documentProviders.providerId not in ("+ providerIds +")";
		}
		
		if(categories != null && categories.length()>0){
			
			categoriesCondition = " and category not in ("+ categories +")";
		}
		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		String query = " SELECT id,statements.providerName as providerName,displayImagePathSmall,read,paid,bill,periodEndDate,"
				+ " max(substr(periodEndDate,7)||substr(periodEndDate,1,2)||substr(periodEndDate,4,2)) as statementDate,showInStatements FROM statements,documentProviders,userDocumentProviders where "
				+ " not statements.periodEndDate='null' and statements.providerName = documentProviders.providerName and documentProviders.providerId = userDocumentProviders.providerId and statements.userId = userDocumentProviders.userId and statements.userId = "
				+ userid
				+ providerCondition +" and bill=0 group by displayImagePathBigAltText order by 2";
		
		
		Cursor cursor = db
				.rawQuery(query, null);

		/*
		 * Cursor cursor = db.query("statement", new String[] {
		 * "id","providerName", "displayImagePathSmall", "read", "paid",
		 * "bill","periodEndDate"}, null, null, null, null, null, null);
		 */
		Statement statement = null;
		// offersListToExpand = new LinkedHashMap<String, ArrayList<Offer>>();

		if (cursor.moveToFirst()) {
			do {
				int i = cursor.getInt(8);
				if(i == 0){
					continue;
				}
				
				Long id = Long.parseLong(cursor.getString(0));

				String 	sql = "SELECT subAccountType, endBalance, itemChecked, subAccountId,category  FROM StatementsSubAccounts where statementID="+ id +" "+ categoriesCondition+" Order by category";
				
				
				Cursor subAccCursor = db.rawQuery(sql, null);
				if (subAccCursor.moveToFirst()) {
					do {
						statement = new Statement();
						statement.setId(id);
						statement.setName(cursor.getString(1));
						statement.setMessageType(subAccCursor.getString(0));
						statement.setMessage("Amount $"
								+ subAccCursor.getString(1));
						statement.setBalance(subAccCursor.getDouble(1));
						String displayImagePathSmall = cursor.getString(2);
						String displayImagePathSmallfileName = displayImagePathSmall
								.substring(displayImagePathSmall
										.lastIndexOf("/") + 1);
						File fdisplayImagePathSmall = new File(activity
								.getBaseContext().getFilesDir(),
								displayImagePathSmallfileName);
						Image image = new Image();
						if (!fdisplayImagePathSmall.exists()) {
							Log.w("OffersService",
									"Calling Download Image for file :"
											+ displayImagePathSmallfileName);
							image.setPath(ImageUtil.downloadImage(activity,
									displayImagePathSmall,
									displayImagePathSmallfileName));
						} else {
							image.setPath(fdisplayImagePathSmall
									.getAbsolutePath());
						}
						statement.setDisplayImagePathSmall(image);
						statement.setRead(Boolean.valueOf(cursor.getString(3)
								.equals("1")));
						statement.setPaid(Boolean.valueOf(cursor.getString(4)
								.equals("1")));
						statement.setBill(Boolean.valueOf(cursor.getString(5)
								.equals("1")));
						statement.setItemChecked(true);
						statement.setSubAccountId(subAccCursor.getLong(3));
						statement.setCategory(subAccCursor.getString(4));
						statement.setStatementDisplayDate(cursor.getString(6));
						statement.setDisplayImagePathSmall(image);
						statementList.add(statement);
					} while (subAccCursor.moveToNext());
				}
				subAccCursor.close();
			} while (cursor.moveToNext());

		}
		cursor.close();
		// db.close();
		return statementList;
	}

	public ArrayList<Statement> getAllAccountStatements(Activity activity,
			Long userid, String sortOrderClause) {
		ArrayList<Statement> statementList = new ArrayList<Statement>();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		String query = "SELECT id,statements.providerName as providerName,displayImagePathSmall,read,paid,bill,periodEndDate FROM statements,documentProviders,userDocumentProviders where statements.bill=0 and statements.showInStatements=1 and statements.providerName = documentProviders.providerName and documentProviders.providerId = userDocumentProviders.providerId and statements.userId = userDocumentProviders.userId and statements.userId = "
				+ userid + " " + sortOrderClause;
		Cursor cursor = db.rawQuery(query, null);
		/*
		 * Cursor cursor = db.query("statement", new String[] {
		 * "id","providerName", "displayImagePathSmall", "read", "paid",
		 * "bill","periodEndDate"}, null, null, null, null, null, null);
		 */
		Statement statement = null;
		// offersListToExpand = new LinkedHashMap<String, ArrayList<Offer>>();

		if (cursor.moveToFirst()) {
			do {

				Long id = Long.parseLong(cursor.getString(0));
				System.out.println("id = " + id);
				Cursor subAccCursor = db
						.rawQuery(
								"SELECT subAccountType, endBalance  FROM StatementsSubAccounts where statementID="
										+ id, null);
				statement = new Statement();
				statement.setId(id);
				statement.setProviderName(cursor.getString(1));
				String displayImagePathSmall = cursor.getString(2);
				String displayImagePathSmallfileName = displayImagePathSmall
						.substring(displayImagePathSmall
								.lastIndexOf("/") + 1);
				File fdisplayImagePathSmall = new File(activity
						.getBaseContext().getFilesDir(),
						displayImagePathSmallfileName);
				Image image = new Image();
				if (!fdisplayImagePathSmall.exists()) {
					Log.w("OffersService", "Calling Download Image for file :"
							+ displayImagePathSmallfileName);
					image.setPath(ImageUtil.downloadImage(activity,
							displayImagePathSmall,
							displayImagePathSmallfileName));
				} else {
					image.setPath(fdisplayImagePathSmall.getAbsolutePath());
				}
				double amount = 0.0;
				statement.setDisplayImagePathSmall(image);
				statement.setRead(Boolean.valueOf(cursor.getString(3).equals(
						"1")));
				statement.setPaid(Boolean.valueOf(cursor.getString(4).equals(
						"1")));
				statement.setBill(Boolean.valueOf(cursor.getString(5).equals(
						"1")));
				statement.setDueDate(cursor.getString(6));
				statement.setStatementDisplayDate("Statement "
						+ cursor.getString(6));
				statement.setDisplayImagePathSmall(image);
				if (subAccCursor.moveToFirst()) {
					do {
						amount += subAccCursor.getDouble(1);

					} while (subAccCursor.moveToNext());
				}
				statement.setDueAmount(amount + "");
				statement.setBalance(amount);
				statementList.add(statement);
				subAccCursor.close();
			} while (cursor.moveToNext());

		}
		cursor.close();
		// db.close();
		return statementList;
	}

	/*
	 * public String downloadImage(Activity activity,String DownloadUrl, String
	 * fileName) { String filePath = null; try { Log.d("DownloadImage",
	 * "download url:" + DownloadUrl); Log.d("DownloadImage",
	 * "download file name:" + fileName);
	 * 
	 * 
	 * URL url = new URL(DownloadUrl); File file = new
	 * File(activity.getBaseContext().getFilesDir(), fileName);
	 * 
	 * long startTime = System.currentTimeMillis();
	 * 
	 * 
	 * URLConnection uconn = url.openConnection(); //uconn.setReadTimeout();
	 * //uconn.setConnectTimeout();
	 * 
	 * InputStream is = uconn.getInputStream(); BufferedInputStream
	 * bufferinstream = new BufferedInputStream(is);
	 * 
	 * ByteArrayBuffer baf = new ByteArrayBuffer(5000); int current = 0; while
	 * ((current = bufferinstream.read()) != -1) { baf.append((byte) current); }
	 * 
	 * FileOutputStream fos = new FileOutputStream(file);
	 * fos.write(baf.toByteArray()); fos.flush(); fos.close();
	 * Log.d("DownloadImage", "download ready in" + ((System.currentTimeMillis()
	 * - startTime) / 1000) + "sec"); int dotindex = fileName.lastIndexOf('.');
	 * if (dotindex >= 0) { fileName = fileName.substring(0, dotindex);
	 * 
	 * } filePath = file.getAbsolutePath(); Log.d("DownloadImage",
	 * "downloaded file path :" + filePath);
	 * 
	 * } catch (Exception e) { Log.d("DownloadImage", "Error:" + e); } return
	 * filePath; }
	 */

	public List<Statement> getAllStatementsForCount(Activity activity,
			long userid) {
		ArrayList<Statement> statementList = new ArrayList<Statement>();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		Cursor cursor = db
				.rawQuery(
						"SELECT id,statements.providerName as providerName,displayImagePathSmall,read,paid,bill,periodEndDate FROM statements,documentProviders,userDocumentProviders where statements.providerName = documentProviders.providerName and documentProviders.providerId = userDocumentProviders.providerId and statements.userId = userDocumentProviders.userId and statements.userId = "
								+ userid, null);
		/*
		 * Cursor cursor = db.query("statement", new String[] {
		 * "id","providerName", "displayImagePathSmall", "read", "paid",
		 * "bill","periodEndDate"}, null, null, null, null, null, null);
		 */
		Statement statement = null;
		// offersListToExpand = new LinkedHashMap<String, ArrayList<Offer>>();

		if (cursor.moveToFirst()) {
			do {

				Long id = Long.parseLong(cursor.getString(0));
				statement = new Statement();
				statement.setId(id);
				statement.setName(cursor.getString(1));

				String displayImagePathSmall = cursor.getString(2);
				String displayImagePathSmallfileName = displayImagePathSmall
						.substring(displayImagePathSmall
								.lastIndexOf("/") + 1);
				File fdisplayImagePathSmall = new File(activity
						.getBaseContext().getFilesDir(),
						displayImagePathSmallfileName);
				Image image = new Image();
				if (!fdisplayImagePathSmall.exists()) {
					Log.w("OffersService", "Calling Download Image for file :"
							+ displayImagePathSmallfileName);
					image.setPath(ImageUtil.downloadImage(activity,
							displayImagePathSmall,
							displayImagePathSmallfileName));
				} else {
					image.setPath(fdisplayImagePathSmall.getAbsolutePath());
				}

				statement.setRead(Boolean.valueOf(cursor.getString(3).equals(
						"1")));
				statement.setPaid(Boolean.valueOf(cursor.getString(4).equals(
						"1")));
				statement.setBill(Boolean.valueOf(cursor.getString(5).equals(
						"1")));
				statement.setDisplayImagePathSmall(image);
				statementList.add(statement);

			} while (cursor.moveToNext());

		}
		cursor.close();
		// db.close();
		return statementList;
	}

	public List<Statement> getAllStatements(Context activity, long userId) {
		ArrayList<Statement> statementList = new ArrayList<Statement>();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		String query = "SELECT id,statements.providerName as providerName,displayImagePathSmall,read,paid,bill,periodEndDate,"
				+ "max(substr(statementDisplayDate ,7)||substr(statementDisplayDate ,1,2)||substr(statementDisplayDate ,4,2)) as statementDate, tagId FROM statements,documentProviders,userDocumentProviders where "
				+ "not statements.statementDisplayDate ='null' and statements.providerName = documentProviders.providerName and documentProviders.providerId = userDocumentProviders.providerId and statements.userId = userDocumentProviders.userId and statements.userId = "
				+ userId
				+ " and statements.bill = 1 group by displayImagePathBigAltText "
				+ " union"
				+ " SELECT id,statements.providerName as providerName,displayImagePathSmall,read,paid,bill,periodEndDate,"
				+ " max(substr(periodEndDate,7)||substr(periodEndDate,1,2)||substr(periodEndDate,4,2)) as statementDate, tagId FROM statements,documentProviders,userDocumentProviders where "
				+ " not statements.periodEndDate='null' and statements.providerName = documentProviders.providerName and documentProviders.providerId = userDocumentProviders.providerId and statements.userId = userDocumentProviders.userId and statements.userId = "
				+ userId
				+ " and statements.bill = 0 group by displayImagePathBigAltText order by 6 desc,2";

		// Cursor cursor =
		// db.rawQuery("SELECT id,providerName,displayImagePathSmall,read,paid,bill,periodEndDate FROM statements where userId = "+userid
		// ,null);
		Cursor cursor = db.rawQuery(query, null);
		/*
		 * Cursor cursor = db.query("statement", new String[] {
		 * "id","providerName", "displayImagePathSmall", "read", "paid",
		 * "bill","periodEndDate"}, null, null, null, null, null, null);
		 */
		Statement statement = null;
		// offersListToExpand = new LinkedHashMap<String, ArrayList<Offer>>();

		if (cursor.moveToFirst()) {
			do {

				Long id = Long.parseLong(cursor.getString(0));
				statement = new Statement();
				statement.setId(id);
				statement.setName(cursor.getString(1));

				String displayImagePathSmall = cursor.getString(2);
				String displayImagePathSmallfileName = displayImagePathSmall
						.substring(displayImagePathSmall
								.lastIndexOf("/") + 1);
				File fdisplayImagePathSmall = new File(activity.getFilesDir(),
						displayImagePathSmallfileName);
				Image image = new Image();
				if (!fdisplayImagePathSmall.exists()) {
					Log.w("OffersService", "Calling Download Image for file :"
							+ displayImagePathSmallfileName);
					image.setPath(ImageUtil.downloadImage(activity,
							displayImagePathSmall,
							displayImagePathSmallfileName));
				} else {
					image.setPath(fdisplayImagePathSmall.getAbsolutePath());
				}

				statement.setRead(Boolean.valueOf(cursor.getString(3).equals(
						"1")));
				statement.setPaid(Boolean.valueOf(cursor.getString(4).equals(
						"1")));
				statement.setBill(Boolean.valueOf(cursor.getString(5).equals(
						"1")));
				statement.setDisplayImagePathSmall(image);
				statement.setTagId(cursor.getInt(8));
				statementList.add(statement);

			} while (cursor.moveToNext());

		}
		cursor.close();
		// db.close();
		return statementList;
	}

	public List<Statement> getAllDirtyStatements(Context activity, long userId) {
		ArrayList<Statement> statementList = new ArrayList<Statement>();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		String query = "SELECT id,providerName,displayImagePathSmall,read,paid,bill,periodEndDate,"
				+ "max(substr(statementDisplayDate ,7)||substr(statementDisplayDate ,4,2)||substr(statementDisplayDate ,1,2)) as statementDate, tagId, isPDFDeleted FROM "
				+ TABLE_STATEMENTS
				+ " where "
				+ "not statementDisplayDate ='null'  and userId = "
				+ userId
				+ " and bill = 1 and isDirty=1 group by displayImagePathBigAltText "
				+ " union"
				+ " SELECT id,providerName,displayImagePathSmall,read,paid,bill,periodEndDate,"
				+ " max(substr(periodEndDate,7)||substr(periodEndDate,4,2)||substr(periodEndDate,1,2)) as statementDate, tagId, isPDFDeleted FROM "
				+ TABLE_STATEMENTS
				+ " where "
				+ " not periodEndDate='null'  and userId = "
				+ userId
				+ " and bill = 0 and isDirty=1 group by displayImagePathBigAltText order by 6 desc,2";

		// Cursor cursor =
		// db.rawQuery("SELECT id,providerName,displayImagePathSmall,read,paid,bill,periodEndDate FROM statements where userId = "+userid
		// ,null);
		Cursor cursor = db.rawQuery(query, null);
		/*
		 * Cursor cursor = db.query("statement", new String[] {
		 * "id","providerName", "displayImagePathSmall", "read", "paid",
		 * "bill","periodEndDate"}, null, null, null, null, null, null);
		 */
		Statement statement = null;
		// offersListToExpand = new LinkedHashMap<String, ArrayList<Offer>>();

		if (cursor.moveToFirst()) {
			do {

				Long id = Long.parseLong(cursor.getString(0));
				statement = new Statement();
				statement.setId(id);
				statement.setName(cursor.getString(1));

				/*
				 * String displayImagePathSmall = cursor.getString(2); String
				 * displayImagePathSmallfileName = displayImagePathSmall
				 * .substring(displayImagePathSmall.lastIndexOf("imageName=") +
				 * 10); File fdisplayImagePathSmall = new
				 * File(activity.getFilesDir(), displayImagePathSmallfileName);
				 * Image image=new Image(); if
				 * (!fdisplayImagePathSmall.exists()) { Log.w("OffersService",
				 * "Calling Download Image for file :" +
				 * displayImagePathSmallfileName);
				 * image.setPath(ImageUtil.downloadImage(activity,
				 * displayImagePathSmall, displayImagePathSmallfileName)); }
				 * else {
				 * image.setPath(fdisplayImagePathSmall.getAbsolutePath()); }
				 */

				statement.setRead(Boolean.valueOf(cursor.getString(3).equals(
						"1")));
				statement.setPaid(Boolean.valueOf(cursor.getString(4).equals(
						"1")));
				statement.setBill(Boolean.valueOf(cursor.getString(5).equals(
						"1")));
				// statement.setDisplayImagePathSmall(image);
				statement.setTagId(cursor.getInt(8));
				statement.setPDFDeleted(Boolean.valueOf(cursor.getString(9)
						.equals("1")));
				statementList.add(statement);

			} while (cursor.moveToNext());

		}
		cursor.close();
		// db.close();
		return statementList;
	}
	
	
	
	public ArrayList<Statement> getAllCashFlowDirtyStatements(Context activity,
			Long userid) {
		ArrayList<Statement> statementList = new ArrayList<Statement>();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		String query = " SELECT id,statements.providerName as providerName,displayImagePathSmall,read,paid,bill,periodEndDate,"
				+ " max(substr(periodEndDate,7)||substr(periodEndDate,1,2)||substr(periodEndDate,4,2)) as statementDate FROM statements,documentProviders,userDocumentProviders where "
				+ " not statements.periodEndDate='null' and statements.providerName = documentProviders.providerName and documentProviders.providerId = userDocumentProviders.providerId and statements.userId = userDocumentProviders.userId and statements.userId = "
				+ userid
				+ " and statements.showInCashFlow=1 and bill=0 and statements.isDirty=1 group by displayImagePathBigAltText order by 2";
		
		
		Cursor cursor = db
				.rawQuery(query, null);

		/*
		 * Cursor cursor = db.query("statement", new String[] {
		 * "id","providerName", "displayImagePathSmall", "read", "paid",
		 * "bill","periodEndDate"}, null, null, null, null, null, null);
		 */
		Statement statement = null;
		// offersListToExpand = new LinkedHashMap<String, ArrayList<Offer>>();

		if (cursor.moveToFirst()) {
			do {

				Long id = Long.parseLong(cursor.getString(0));
				Cursor subAccCursor = db
						.rawQuery(
								"SELECT subAccountType, endBalance, itemChecked, subAccountId  FROM StatementsSubAccounts where statementID="
										+ id, null);
				if (subAccCursor.moveToFirst()) {
					do {
						statement = new Statement();
						statement.setId(id);
						statement.setName(cursor.getString(1));
						statement.setMessageType(subAccCursor.getString(0));
						statement.setMessage("Amount $"
								+ subAccCursor.getString(1));
						statement.setBalance(subAccCursor.getDouble(1));
						/*String displayImagePathSmall = cursor.getString(2);
						String displayImagePathSmallfileName = displayImagePathSmall
								.substring(displayImagePathSmall
										.lastIndexOf("/") + 1);
						File fdisplayImagePathSmall = new File(activity
								.getBaseContext().getFilesDir(),
								displayImagePathSmallfileName);
						Image image = new Image();
						if (!fdisplayImagePathSmall.exists()) {
							Log.w("OffersService",
									"Calling Download Image for file :"
											+ displayImagePathSmallfileName);
							image.setPath(ImageUtil.downloadImage(activity,
									displayImagePathSmall,
									displayImagePathSmallfileName));
						} else {
							image.setPath(fdisplayImagePathSmall
									.getAbsolutePath());
						}
						statement.setDisplayImagePathSmall(image);*/
						statement.setRead(Boolean.valueOf(cursor.getString(3)
								.equals("1")));
						statement.setPaid(Boolean.valueOf(cursor.getString(4)
								.equals("1")));
						statement.setBill(Boolean.valueOf(cursor.getString(5)
								.equals("1")));
						statement.setItemChecked(Boolean.valueOf(subAccCursor.getString(2)
								.equals("1")));
						statement.setSubAccountId(subAccCursor.getLong(3));
						//statement.setDisplayImagePathSmall(image);
						statementList.add(statement);
					} while (subAccCursor.moveToNext());
				}
				subAccCursor.close();
			} while (cursor.moveToNext());

		}
		cursor.close();
		// db.close();
		return statementList;
	}


	public List<Statement> getBillStatements(Activity activity, long userid,String sortByClause,String categories, String providerIds) {
		ArrayList<Statement> statementList = new ArrayList<Statement>();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();
		String category = "";
		if(categories != null && categories.length() > 0){
			category=" and statements.category not in (" + categories + ") ";
		}
		
		String providerCondition = "";
		if(providerIds != null && providerIds.length()>0){
			
			providerCondition = " and documentProviders.providerId not in ("+ providerIds +")  ";
		}
		
		
		
		String query = "select id, tmp1.providerName as providerName,displayImagePathSmall,read,paid,bill,periodEndDate,dueDate,balance,statementDisplayDate,minAmountDue,category, "
				  +"(substr(statementDisplayDate, 7, 4) || '-' || substr(statementDisplayDate, 1, 2)|| '-' || substr(statementDisplayDate, 4, 2)) as statementDisplayDateStr, "
				  +"(substr(dueDate, 7, 4)|| '-' || substr(dueDate, 1, 2)|| '-' || substr(dueDate, 4, 2)) as dueDateStr "
				  +"from "
				  +" (SELECT * FROM statements,documentProviders,userDocumentProviders " 
				  +"	where " 
				  +"	statements.bill=1 " 
				  +"	and statements.showInBill=1 " 
				  +"	and statements.providerName = documentProviders.providerName " 
				  +"	and documentProviders.providerId = userDocumentProviders.providerId" 
				  +"	and statements.userId = userDocumentProviders.userId " + providerCondition + category 
				  +"	and statements.userId ="+ userid+ ") as tmp1 "
				  +"where " 
				  +"(select count(*) FROM statements s1 "
				  +"where " 
				  +"tmp1.providerName = s1.providerName "
				  +"and "
				  +"substr(tmp1.statementDisplayDate, 7, 4) || '-' || substr(tmp1.statementDisplayDate, 1, 2)|| '-' || substr(tmp1.statementDisplayDate, 4, 2) "
				  +" <=substr(s1.statementDisplayDate, 7, 4) || '-' || substr(s1.statementDisplayDate, 1, 2)|| '-' || substr(s1.statementDisplayDate, 4, 2) "
				  +")<=2 "
				  
				  +" union "
				  //This query is to fetch the unpaid provider bills.
				  +"SELECT id, statements.providerName as providerName, displayImagePathSmall, read, paid, bill, periodEndDate, dueDate, balance, statementDisplayDate , "
				  +"minAmountDue, category, (substr(statementDisplayDate, 7, 4) || '-' || substr(statementDisplayDate, 1, 2)|| '-' || substr(statementDisplayDate, 4, 2)) as statementDisplayDateStr, "
				  +"(substr(dueDate, 7, 4)|| '-' || substr(dueDate, 1, 2)|| '-' || substr(dueDate, 4, 2)) as dueDateStr " 
				  +"FROM statements,documentProviders,userDocumentProviders  "
				  +"where "  
				  +"statements.bill=1 "  
				  +"and statements.showInBill=1 "  
				  +"and statements.providerName = documentProviders.providerName "  
				  +"and documentProviders.providerId = userDocumentProviders.providerId " 
				  +"and statements.userId = userDocumentProviders.userId " + providerCondition + category 
				  +"and statements.userId = "+ userid+" " 
				  +"and statements.paid=0 "
				  + sortByClause;
		
		Cursor cursor = db.rawQuery(query, null);
		/*
		 * Cursor cursor = db.query("statement", new String[] {
		 * "id","providerName", "displayImagePathSmall", "read", "paid",
		 * "bill","periodEndDate"}, null, null, null, null, null, null);
		 */
		Statement statement = null;
		// offersListToExpand = new LinkedHashMap<String, ArrayList<Offer>>();

		if (cursor.moveToFirst()) {
			do {

				Long id = Long.parseLong(cursor.getString(0));
				statement = new Statement();
				statement.setId(id);
				statement.setProviderName(cursor.getString(1));

				String displayImagePathSmall = cursor.getString(2);
				String displayImagePathSmallfileName = displayImagePathSmall
						.substring(displayImagePathSmall
								.lastIndexOf("/") + 1);
				File fdisplayImagePathSmall = new File(activity
						.getBaseContext().getFilesDir(),
						displayImagePathSmallfileName);
				Image image = new Image();
				if (!fdisplayImagePathSmall.exists()) {
					Log.w("OffersService", "Calling Download Image for file :"
							+ displayImagePathSmallfileName);
					image.setPath(ImageUtil.downloadImage(activity,
							displayImagePathSmall,
							displayImagePathSmallfileName));
				} else {
					image.setPath(fdisplayImagePathSmall.getAbsolutePath());
				}

				statement.setRead(Boolean.valueOf(cursor.getString(3).equals(
						"1")));
				statement.setPaid(Boolean.valueOf(cursor.getString(4).equals(
						"1")));
				statement.setBill(Boolean.valueOf(cursor.getString(5).equals(
						"1")));
				statement.setDisplayImagePathSmall(image);

				statement.setDueDate(cursor.getString(7));
				statement.setBalance(cursor.getDouble(8));
				statement.setStatementDisplayDate("Bill • "
						+ cursor.getString(9));
				statement.setMinAmountDue(cursor.getDouble(10));
				statement.setCategory(cursor.getString(11));
				statementList.add(statement);

			} while (cursor.moveToNext());

		}
		cursor.close();
		// db.close();
		return statementList;
	}

	public List<Statement> getStatements(Activity activity, long userid,
			String sortBy) {
		ArrayList<Statement> statementList = new ArrayList<Statement>();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		String query = "SELECT id,statements.providerName as providerName,displayImagePathSmall,read,paid,bill,periodEndDate,dueDate,balance,statementDisplayDate,tagId FROM statements,documentProviders,userDocumentProviders where statements.providerName = documentProviders.providerName and documentProviders.providerId = userDocumentProviders.providerId and statements.userId = userDocumentProviders.userId and statements.userId = "
				+ userid + " " + sortBy;
		Cursor cursor = db.rawQuery(query, null);
		/*
		 * Cursor cursor = db.query("statement", new String[] {
		 * "id","providerName", "displayImagePathSmall", "read", "paid",
		 * "bill","periodEndDate"}, null, null, null, null, null, null);
		 */
		Statement statement = null;
		// offersListToExpand = new LinkedHashMap<String, ArrayList<Offer>>();

		if (cursor.moveToFirst()) {
			do {

				Long id = Long.parseLong(cursor.getString(0));
				System.out.println("id = " + id);
				if (Boolean.valueOf(cursor.getString(5).equals("0"))) {
					Cursor subAccCursor = db
							.rawQuery(
									"SELECT subAccountType, endBalance  FROM StatementsSubAccounts where statementID="
											+ id, null);
					statement = new Statement();
					statement.setId(id);
					statement.setProviderName(cursor.getString(1));
					String displayImagePathSmall = cursor.getString(2);
					String displayImagePathSmallfileName = displayImagePathSmall
							.substring(displayImagePathSmall
									.lastIndexOf("/") + 1);
					File fdisplayImagePathSmall = new File(activity
							.getBaseContext().getFilesDir(),
							displayImagePathSmallfileName);
					Image image = new Image();
					if (!fdisplayImagePathSmall.exists()) {
						Log.w("OffersService",
								"Calling Download Image for file :"
										+ displayImagePathSmallfileName);
						image.setPath(ImageUtil.downloadImage(activity,
								displayImagePathSmall,
								displayImagePathSmallfileName));
					} else {
						image.setPath(fdisplayImagePathSmall.getAbsolutePath());
					}
					double amount = 0.0;
					statement.setDisplayImagePathSmall(image);
					statement.setRead(Boolean.valueOf(cursor.getString(3)
							.equals("1")));
					statement.setPaid(Boolean.valueOf(cursor.getString(4)
							.equals("1")));
					statement.setBill(Boolean.valueOf(cursor.getString(5)
							.equals("1")));
					statement.setDueDate(cursor.getString(6));
					statement.setStatementDisplayDate("Statement • "
							+ cursor.getString(6));
					statement.setDisplayImagePathSmall(image);
					if (subAccCursor.moveToFirst()) {
						do {
							amount += subAccCursor.getDouble(1);

						} while (subAccCursor.moveToNext());
					}
					statement.setDueAmount(amount + "");
					statement.setBalance(amount);
					statementList.add(statement);
					subAccCursor.close();
				} else {
					id = Long.parseLong(cursor.getString(0));
					statement = new Statement();
					statement.setId(id);
					statement.setProviderName(cursor.getString(1));

					String displayImagePathSmall = cursor.getString(2);
					String displayImagePathSmallfileName = displayImagePathSmall
							.substring(displayImagePathSmall
									.lastIndexOf("/") + 1);
					File fdisplayImagePathSmall = new File(activity
							.getBaseContext().getFilesDir(),
							displayImagePathSmallfileName);
					Image image = new Image();
					if (!fdisplayImagePathSmall.exists()) {
						Log.w("OffersService",
								"Calling Download Image for file :"
										+ displayImagePathSmallfileName);
						image.setPath(ImageUtil.downloadImage(activity,
								displayImagePathSmall,
								displayImagePathSmallfileName));
					} else {
						image.setPath(fdisplayImagePathSmall.getAbsolutePath());
					}

					statement.setRead(Boolean.valueOf(cursor.getString(3)
							.equals("1")));
					statement.setPaid(Boolean.valueOf(cursor.getString(4)
							.equals("1")));
					statement.setBill(Boolean.valueOf(cursor.getString(5)
							.equals("1")));
					statement.setDisplayImagePathSmall(image);

					statement.setDueDate(cursor.getString(7));
					statement.setBalance(cursor.getDouble(8));
					statement.setStatementDisplayDate("Bill • "
							+ cursor.getString(9));
					statement.setTagId(cursor.getInt(10));
					statementList.add(statement);
				}
			} while (cursor.moveToNext());

		}
		cursor.close();
		/*
		 * query=
		 * "SELECT id,providerName,displayImagePathSmall,read,paid,bill,periodEndDate FROM statements where bill=1 and userID="
		 * +userid+" "+ sortBy; cursor = db.rawQuery(query,null);
		 */
		/*
		 * Cursor cursor = db.query("statement", new String[] {
		 * "id","providerName", "displayImagePathSmall", "read", "paid",
		 * "bill","periodEndDate"}, null, null, null, null, null, null);
		 */
		// offersListToExpand = new LinkedHashMap<String, ArrayList<Offer>>();

		/*
		 * if (cursor.moveToFirst()) { do {
		 * 
		 * Long id=Long.parseLong(cursor.getString(0)); statement = new
		 * Statement(); statement.setId(id);
		 * statement.setProviderName(cursor.getString(1));
		 * 
		 * String displayImagePathSmall = cursor.getString(2); String
		 * displayImagePathSmallfileName = displayImagePathSmall
		 * .substring(displayImagePathSmall.lastIndexOf("/") + 1); File
		 * fdisplayImagePathSmall = new
		 * File(activity.getBaseContext().getFilesDir(),
		 * displayImagePathSmallfileName); Image image=new Image(); if
		 * (!fdisplayImagePathSmall.exists()) { Log.w("OffersService",
		 * "Calling Download Image for file :" + displayImagePathSmallfileName);
		 * image.setPath(ImageUtil.downloadImage(activity,
		 * displayImagePathSmall, displayImagePathSmallfileName)); } else {
		 * image.setPath(fdisplayImagePathSmall.getAbsolutePath()); }
		 * 
		 * statement.setRead(Boolean.valueOf(cursor.getString(3).equals("1")));
		 * statement.setPaid(Boolean.valueOf(cursor.getString(4).equals("1")));
		 * statement.setBill(Boolean.valueOf(cursor.getString(5).equals("1")));
		 * statement.setDisplayImagePathSmall(image);
		 * 
		 * statement.setDueDate(cursor.getString(7));
		 * statement.setBalance(cursor.getDouble(8));
		 * statement.setStatementDisplayDate("Statement "+cursor.getString(9));
		 * statementList.add(statement);
		 * 
		 * } while (cursor.moveToNext());
		 * 
		 * } cursor.close();
		 */
		// db.close();
		return statementList;
	}

	public List<Statement> getUnpaidStatements(Activity activity, long userid) {
		ArrayList<Statement> statementList = new ArrayList<Statement>();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		Cursor cursor = db
				.rawQuery(
						"SELECT id,statements.providerName as providerName,displayImagePathSmall,read,paid,bill,periodEndDate,dueDate,balance,statementDisplayDate FROM statements,documentProviders,userDocumentProviders where statements.bill=1 and statements.showInUnpaid=1 and statements.providerName = documentProviders.providerName and documentProviders.providerId = userDocumentProviders.providerId and statements.userId = userDocumentProviders.userId and statements.userId = "
								+ userid, null);
		/*
		 * Cursor cursor = db.query("statement", new String[] {
		 * "id","providerName", "displayImagePathSmall", "read", "paid",
		 * "bill","periodEndDate"}, null, null, null, null, null, null);
		 */
		Statement statement = null;
		// offersListToExpand = new LinkedHashMap<String, ArrayList<Offer>>();

		if (cursor.moveToFirst()) {
			do {

				Long id = Long.parseLong(cursor.getString(0));
				statement = new Statement();
				statement.setId(id);
				statement.setProviderName(cursor.getString(1));

				String displayImagePathSmall = cursor.getString(2);
				String displayImagePathSmallfileName = displayImagePathSmall
						.substring(displayImagePathSmall
								.lastIndexOf("/") + 1);
				File fdisplayImagePathSmall = new File(activity
						.getBaseContext().getFilesDir(),
						displayImagePathSmallfileName);
				Image image = new Image();
				if (!fdisplayImagePathSmall.exists()) {
					Log.w("OffersService", "Calling Download Image for file :"
							+ displayImagePathSmallfileName);
					image.setPath(ImageUtil.downloadImage(activity,
							displayImagePathSmall,
							displayImagePathSmallfileName));
				} else {
					image.setPath(fdisplayImagePathSmall.getAbsolutePath());
				}

				statement.setRead(Boolean.valueOf(cursor.getString(3).equals(
						"1")));
				statement.setPaid(Boolean.valueOf(cursor.getString(4).equals(
						"1")));
				statement.setBill(Boolean.valueOf(cursor.getString(5).equals(
						"1")));
				statement.setDisplayImagePathSmall(image);

				statement.setDueDate(cursor.getString(7));
				statement.setBalance(cursor.getDouble(8));
				statementList.add(statement);

			} while (cursor.moveToNext());

		}
		cursor.close();
		// db.close();
		return statementList;
	}

	public Statement getStatementByID(Activity activity, long statmentID) {

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		Cursor cursor = db
				.rawQuery(
						"SELECT id,providerName,displayImagePathSmall,read,paid,bill,periodEndDate,dueDate,balance,statementDisplayDate FROM "
								+ TABLE_STATEMENTS
								+ " where id = "
								+ statmentID, null);
		/*
		 * Cursor cursor = db.query("statement", new String[] {
		 * "id","providerName", "displayImagePathSmall", "read", "paid",
		 * "bill","periodEndDate"}, null, null, null, null, null, null);
		 */
		Statement statement = null;
		// offersListToExpand = new LinkedHashMap<String, ArrayList<Offer>>();

		if (cursor.moveToFirst()) {
			do {

				Long id = Long.parseLong(cursor.getString(0));
				statement = new Statement();
				statement.setId(id);
				statement.setProviderName(cursor.getString(1));

				String displayImagePathSmall = cursor.getString(2);
				String displayImagePathSmallfileName = displayImagePathSmall
						.substring(displayImagePathSmall
								.lastIndexOf("/") + 1);
				File fdisplayImagePathSmall = new File(activity
						.getBaseContext().getFilesDir(),
						displayImagePathSmallfileName);
				Image image = new Image();
				if (!fdisplayImagePathSmall.exists()) {
					Log.w("OffersService", "Calling Download Image for file :"
							+ displayImagePathSmallfileName);
					image.setPath(ImageUtil.downloadImage(activity,
							displayImagePathSmall,
							displayImagePathSmallfileName));
				} else {
					image.setPath(fdisplayImagePathSmall.getAbsolutePath());
				}

				statement.setRead(Boolean.valueOf(cursor.getString(3).equals(
						"1")));
				statement.setPaid(Boolean.valueOf(cursor.getString(4).equals(
						"1")));
				statement.setBill(Boolean.valueOf(cursor.getString(5).equals(
						"1")));
				statement.setDisplayImagePathSmall(image);

				statement.setDueDate(cursor.getString(7));
				statement.setBalance(cursor.getDouble(8));

			} while (cursor.moveToNext());

		}
		cursor.close();
		// db.close();
		return statement;
	}

	public int updatePaid(long id, int paid, Activity activity, int isDirty) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("paid", paid);
		values.put("isDirty", isDirty);

		// updating row
		return db.update(TABLE_STATEMENTS, values, "ID = ?",
				new String[] { String.valueOf(id) });
	}


	public void updateCashFlow(long subAccountId, int itemChecked, Activity activity, int isDirty, long userId, long statementId) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		/*ContentValues values = new ContentValues();
		values.put("itemChecked", itemChecked);
		values.put("isDirty", isDirty);

		// updating row
		return db.update(TABLE_STATEMENTS, values, "ID = ? and userId = ?",
				new String[] { String.valueOf(id),  String.valueOf(userId)})*/
		String updateStatementsSubAccountsQuery = "update StatementsSubAccounts set itemChecked = "
				+ itemChecked + " where subAccountId=" + subAccountId + " and userID="
				+ userId;

		String updateStatementQuery = "update Statements set isDirty = "
				+ isDirty + " where ID=" + statementId + " and userID="
				+ userId;

		
		db.execSQL(updateStatementsSubAccountsQuery);
		db.execSQL(updateStatementQuery);
		
				
	}

	
	
	
	
	public int updateShowInAssistance(long id, int showInScreen, String column,
			Activity activity) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(column, showInScreen);

		// updating row
		return db.update(TABLE_STATEMENTS, values, "ID = ?",
				new String[] { String.valueOf(id) });
	}

	public int getTagId(Activity activity, long loginUserId,
			long currentDocumentId) {
		int tagId = 0;
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		String query = "select tagId from " + TABLE_STATEMENTS
				+ " where userID=" + loginUserId + " and ID = "
				+ currentDocumentId;

		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			tagId = cursor.getInt(0);
		}
		cursor.close();

		return tagId;
	}

	public void setTagToStatement(long currentDocumentId, long userId,
			int tagId, Activity activity) {

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		String updateQuery = "update " + TABLE_STATEMENTS + " set tagId = "
				+ tagId + " where ID=" + currentDocumentId + " and userID="
				+ userId;

		db.execSQL(updateQuery);

	}

	public void resetTagToStatement(long currentDocumentId, long userId,
			Activity activity) {

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();

		String updateQuery = "update " + TABLE_STATEMENTS
				+ " set tagId = 0 where ID=" + currentDocumentId
				+ " and userID=" + userId;

		db.execSQL(updateQuery);

	}

	public int getTagIdFromStatement(String currentDocumentId,
			Activity activity, long userId) {
		List<Tag> tagsList = new ArrayList<Tag>();

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);

		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		int tagId = 0;
		String query = "select tagId from " + TABLE_STATEMENTS
				+ " where ID = '" + currentDocumentId + "' and userId="
				+ userId;
		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			tagId = cursor.getInt(0);
		}

		cursor.close();

		return tagId;
	}

	public static void deleteStatements(String statementIds, Context activity,
			long userId) {
		if (statementIds != null && statementIds.length() > 0) {
			DatabaseHandler databaseHandler = DatabaseHandler
					.getDatabaseHandlerInstance(activity);

			SQLiteDatabase db = databaseHandler.getWritableDatabase();

			String deleteSubAccountsQuery = "delete from "
					+ TABLE_STATEMENTS_SUB_ACCOUNTS + " where statementId in("
					+ statementIds + ") and userId=" + userId;
			String deleteTelPhoneStatementAccPlanQuery = "delete from "
					+ TABLE_TELEPHONE_STATEMENT_ACCOUNT_PLAN
					+ " where statementId in(" + statementIds + ") and userId="
					+ userId;
			String deleteTelPhoneStatementQuery = "delete from "
					+ TABLE_TELPHONE_STATEMENT + " where statementId in("
					+ statementIds + ") and userId=" + userId;
			String deleteStatementsQuery = "delete from " + TABLE_STATEMENTS
					+ " where Id in(" + statementIds + ") and userId=" + userId;

			db.execSQL(deleteSubAccountsQuery);
			db.execSQL(deleteTelPhoneStatementAccPlanQuery);
			db.execSQL(deleteTelPhoneStatementQuery);
			db.execSQL(deleteStatementsQuery);
		}
	}

	public boolean tagExistsUserIdAndDocumentId(long userId, long documentId,
			Activity activity) {

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		String countQuery = "SELECT  tagId FROM " + TABLE_STATEMENTS
				+ " WHERE userId=" + userId + " AND Id=" + documentId;
		// Cursor cursor = db.rawQuery(countQuery, null);
		boolean isTagExists = false;

		Cursor cursor = db.rawQuery(countQuery, null);
		int tagId = 0;
		if (cursor.moveToFirst()) {
			tagId = cursor.getInt(0);
			if (tagId != 0) {
				isTagExists = true;
			}
		}

		cursor.close();

		return isTagExists;
	}

	public Tag getTagWithUserIdAndDocumentId(long userId, long documentId,
			Activity activity) {

		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(activity);
		SQLiteDatabase db = databaseHandler.getReadableDatabase();

		String query = "select t.tagId, t.colorCode,t.name,t.userId from tags t, statements s where t.tagId = s.tagid and t.userId=s.userId and s.userId="
				+ userId + " and s.Id=" + documentId;

		Cursor cursor = db.rawQuery(query, null);

		long tagId = 0;
		String colorCode = "";
		String name = "";
		//int sequenceId = 0;

		if (cursor.moveToFirst()) {
			tagId = cursor.getLong(0);
			colorCode = cursor.getString(0);
			name = cursor.getString(2);
			//sequenceId = cursor.getInt(3);
		}

		cursor.close();

		Tag tag = new Tag();
		tag.setTagId(tagId);
		tag.setColorCode(colorCode);
		tag.setName(name);
		//tag.setSequenceId(sequenceId);

		return tag;
	}

	public static void resetDirtyFlagToFalse(String statementIds,
			Context activity, long userId) {
		if (statementIds != null && statementIds.length() > 0) {
			DatabaseHandler databaseHandler = DatabaseHandler
					.getDatabaseHandlerInstance(activity);

			SQLiteDatabase db = databaseHandler.getWritableDatabase();

			String updateDirtyFlagStatementsQuery = "update statements set isDirty = 0 where id in("
					+ statementIds + ") and userID=" + userId;
			db.execSQL(updateDirtyFlagStatementsQuery);
		}
	}

	public static void addCashFlowCategories(Context context, String strAccountTypeId, String strAccountType, String strDisplayName, String strCategoryType) {
		DatabaseHandler databaseHandler = DatabaseHandler
				.getDatabaseHandlerInstance(context);

		SQLiteDatabase db = databaseHandler.getWritableDatabase();
		
		String deleteStatementsCategory = "delete from "
				+ TABLE_STATEMENTS_CATEGORY + " where accountTypeId ="+strAccountTypeId;
		db.execSQL(deleteStatementsCategory);
		
		ContentValues values = new ContentValues();
		values.put("accountTypeId", strAccountTypeId);
		values.put("accountType", strAccountType);
		values.put("displayName", strDisplayName);
		values.put("categoryType", strCategoryType);

		// Inserting Row
		db.insert(TABLE_STATEMENTS_CATEGORY, null, values);

	}
	
	public ArrayList<StatementCategory> getStatementCategory(Context context, String strCategoryType){
		ArrayList<StatementCategory> statementCategoryList = new ArrayList<StatementCategory>();
		StatementCategory statementCategory = null;
		DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseHandlerInstance(context);
		SQLiteDatabase db = databaseHandler.getReadableDatabase();
		String query = " SELECT accountTypeId,accountType,displayName,categoryType FROM StatementsCategory where categoryType="+"'"+strCategoryType+"'";
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			do {
				statementCategory = new StatementCategory();

				Long accountTypeId = Long.parseLong(cursor.getString(0));
				String accountType = cursor.getString(1);
				String displayName = cursor.getString(2);
				String categoryType = cursor.getString(3);
				
				statementCategory.setAccountTypeId(accountTypeId);
				statementCategory.setAccountType(accountType);
				statementCategory.setDisplayName(displayName);
				statementCategory.setCategoryType(categoryType);
				statementCategory.setItemSelected(true);
				
				statementCategoryList.add(statementCategory);
			} while (cursor.moveToNext());
		}
		cursor.close();
		
		return statementCategoryList;
	}
	
	

}