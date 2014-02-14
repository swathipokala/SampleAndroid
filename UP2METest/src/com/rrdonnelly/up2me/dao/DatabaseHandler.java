package com.rrdonnelly.up2me.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class DatabaseHandler extends SQLiteOpenHelper {
	 
    // All Static variables
    private static DatabaseHandler databaseHandler = null;
    
    public static DatabaseHandler getDatabaseHandlerInstance(Context context){
    	
    	if(databaseHandler==null){
    		System.out.println("Database handler is null");
    		databaseHandler = new DatabaseHandler(context.getApplicationContext());
    	}
    	
    	return databaseHandler;
    }
 
    private DatabaseHandler(Context context) {
        //super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //super(context, Environment.getExternalStorageDirectory() + "/path/to/database/on/sdcard/database.sqlite", null, 1);
        super(context, "/data/data/com.rrdonnelly.up2me/databases/Up2Me.sqlite", null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.CREATE_IF_NECESSARY);
    	//super(context, Environment.getExternalStorageDirectory()+"/Up2Me.sqlite", null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
        databaseHandler = this;
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String users = "CREATE TABLE if not exists users (id Long , username VARCHAR, password VARCHAR,zipcode VARCHAR, firstname varchar  DEFAULT (null), lastname varchar  DEFAULT (null),streeaddress1 varchar DEFAULT (null),streeaddress2 varchar DEFAULT (null), city varchar DEFAULT (null), state varchar DEFAULT (null),mobileno varchar DEFAULT (null),myplanurl varchar default null, mycreditcard varchar default null )";
        //String Statements = "CREATE TABLE if not exists Statements (ID TEXT,name TEXT,displayImagePathBig varchar DEFAULT (null) ,displayImagePathBigAltText varchar DEFAULT (null) ,displayImagePathSmall varchar DEFAULT (null) ,dispalyImagePathSmallAltText varchar DEFAULT (null) ,documentPath varchar DEFAULT (null) ,entityPreviewImagePath varchar DEFAULT (null) ,message TEXT,messageType TEXT,statementDisplayDate TEXT DEFAULT (null) ,subAccount INTEGER,periodEndDate TEXT DEFAULT (null) ,dueDate TEXT DEFAULT (null) ,minAmountDue FLOAT,balance FLOAT,providerName TEXT,valid BOOL,telephoneStatements INTEGER DEFAULT (null) ,read BOOL,paid BOOL,bill BOOL,userID INTEGER,displayImageBigFilePath VARCHAR,displayImageSmallFilePath VARCHAR,entityPreviewImageFilePath VARCHAR, tagId INTEGER DEFAULT 0)";
        String cloudProvider = "CREATE TABLE if not exists CloudProvider (clientId INTEGER , providerName VARCHAR, displayImagePath VARCHAR, displayImagePathAltText VARCHAR)";
        String cloudService = "CREATE TABLE if not exists CloudService (cloudProviderID DOUBLE, clientID DOUBLE, displayImagePath VARCHAR, displayImagePathAltText VARCHAR)";
        String clubOffer = "CREATE TABLE if not exists ClubOffer (id INTEGER , clientId INTEGER,displayImagePath VARCHAR, displayImagePathAltText VARCHAR)";
        String clubStatement = "CREATE TABLE if not exists ClubStatement (id INTEGER , name VARCHAR, type VARCHAR)";
        String contractTerminationFee = "CREATE TABLE if not exists ContractTerminationFee (providerId INTEGER, providerName TEXT, fees FLOAT, userID INTEGER)";
        String contractTerminationFeesList = "CREATE TABLE if not exists ContractTerminationFeesList (providerId INTEGER,fees FLOAT)";
        String dataPlanLines = "CREATE TABLE if not exists DataPlanLines (dataPlanID INTEGER, planline INTEGER, userID INTEGER)";
        String dataPlanProviderNames = "CREATE TABLE if not exists DataPlanProviderNames (dataPlanProviderName varchar)";
        String dataPlanTypes = "CREATE TABLE if not exists DataPlanTypes (planType varchar)";
        String dataPlans = "CREATE TABLE if not exists DataPlans (providerName TEXT, dataValue TEXT, dataUnit VARCHAR, planType TEXT, dataPlanID INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , userID INTEGER, minutesValue TEXT, textMessageValue TEXT, imageURLBig TEXT, imageURLIcon TEXT, imageURLSmall TEXT)";
        String offersService = "CREATE TABLE if not exists OffersService (offerID INTEGER NOT NULL, name VARCHAR, expiresOnDisplayDate DATETIME, isRead BOOL, barcodeImagePath VARCHAR, barcodeImagePathAltText VARCHAR, offerImagePath VARCHAR, offerImagePathAltText VARCHAR, teaserImagePath VARCHAR, teaserImagePathAltText VARCHAR, displayImagePathSmall VARCHAR, displayImagePathSmallAltText VARCHAR, offerText TEXT, createdDate DATETIME, userID INTEGER, tagId INTEGER DEFAULT 0, isDirty BOOL DEFAULT 0)";
        String offersZipCodes = "CREATE TABLE if not exists OffersZipCodes (offerID INTEGER, zipCode INTEGER, userID INTEGER)";
        String phonesService = "CREATE TABLE if not exists PhonesService (providerID DOUBLE, providerName VARCHAR, phoneID INTEGER, manufacturer VARCHAR, name VARCHAR, color VARCHAR, dimensionsInInches VARCHAR, operatingsystem VARCHAR, processor VARCHAR, connectionspeed VARCHAR, displayResolution VARCHAR, storageMemoryInGB FLOAT, cameraMegapixels FLOAT, batteryLifeInHours FLOAT, priceWithContract FLOAT, weightInOunces FLOAT, availableDate DATETIME, displaySizeInInches FLOAT)";
        String statementsCost = "CREATE TABLE if not exists StatementsCost (year INTEGER, month VARCHAR, dataCost FLOAT, textUsage DOUBLE, totalCost DOUBLE, minutesUsed DOUBLE, lineCount INTEGER, contractStartDate DATETIME, contractEndDate DATETIME, providerName VARCHAR, planCost VARCHAR, accessDiscount DOUBLE, dataOverageCost DOUBLE, minutesOverageCost DOUBLE, textOverageCost DOUBLE, userID INTEGER)";
        String statementsSubAccounts = "CREATE TABLE if not exists StatementsSubAccounts (subAccountId INTEGER DEFAULT 0, statementId INTEGER DEFAULT 0, subAccountName VARCHAR, subAccountType VARCHAR, endBalance FLOAT, subAccountNumber VARCHAR, userId INTEGER, itemChecked BOOL DEFAULT 0,category VARCHAR DEFAULT (null))";
        String subscriptionOffer = "CREATE TABLE if not exists SubscriptionOffer (soID INTEGER NOT NULL , name VARCHAR, displayImagePath VARCHAR, displayImagePathAltText VARCHAR, subscribed BOOL, isOffer BOOL, isBill BOOL,isAccount BOOL,userID INTEGER)";
        String telephoneStatementAccountPlan = "CREATE TABLE if not exists TelephoneStatementAccountPlan (telephoneStatementId INTEGER, accountPlan varchar, statementId INTEGER, userId INTEGER)";
        String telphoneStatement = "CREATE TABLE if not exists TelphoneStatement (telephoneStatementId INTEGER, planName VARCHAR, accountPlan VARCHAR, statementId INTEGER, userId INTEGER)";
        //String tags =  "CREATE TABLE Tags(tagId INTEGER PRIMARY KEY, colorCode VARCHAR, name VARCHAR, sequenceId INTEGER, userId INTEGER)";
        String tags =  "CREATE TABLE Tags(tagId INTEGER, colorCode VARCHAR, name VARCHAR, userId INTEGER)";
        String userSettings =  "CREATE TABLE UserSettings(id Long , showBadgesStatements BOOL,showBadgesBills BOOL,showBadgesOffers BOOL,showBadgesLibrary BOOL,calendarAlertsBills BOOL,calendarAlertsOffers BOOL,cloudSyncStatements BOOL,cloudSyncBills BOOL,cloudSyncAll BOOL,cloudSyncAppData BOOL,passwordChecked BOOL,setTimeOut INTEGER)";
        String offerProviders = "CREATE TABLE if not exists OfferProviders (providerId INTEGER , providerName VARCHAR, isActive BOOL, imageUrlSmallStr VARCHAR)";
        String userofferProviders = "CREATE TABLE if not exists UserOfferProviders (userId INTEGER, providerId INTEGER)";
        String documentProviders = "CREATE TABLE if not exists DocumentProviders (providerId INTEGER , providerName VARCHAR, isActive BOOL, imageUrlSmallStr VARCHAR)";
        String userDocumentProviders = "CREATE TABLE if not exists UserDocumentProviders (userId INTEGER, providerId INTEGER)";
        String state = "CREATE TABLE if not exists state (stateId INTEGER, stateCode VARCHAR, stateName VARCHAR, countryId INTEGER, countryName VARCHAR, countryCode VARCHAR, isActive BOOL)";
        String statements = "CREATE TABLE Statements (Id INTEGER,name VARCHAR,displayImagePathBig VARCHAR DEFAULT (null) ,displayImagePathBigAltText VARCHAR DEFAULT (null) ,displayImagePathSmall VARCHAR DEFAULT (null) ,dispalyImagePathSmallAltText VARCHAR DEFAULT (null) ,documentPath VARCHAR DEFAULT (null) ,entityPreviewImagePath VARCHAR DEFAULT (null) ,message VARCHAR,messageType VARCHAR,statementDisplayDate VARCHAR DEFAULT (null) ,periodEndDate VARCHAR DEFAULT (null) ,dueDate VARCHAR DEFAULT (null) ,minAmountDue FLOAT,balance FLOAT,providerName VARCHAR,valid BOOL,read BOOL,paid BOOL,bill BOOL,userId INTEGER,displayImageBigFilePath VARCHAR,displayImageSmallFilePath VARCHAR,entityPreviewImageFilePath VARCHAR, tagId INTEGER DEFAULT 0,documentCoverPath VARCHAR DEFAULT (null), providerId INTEGER DEFAULT 0,userPrimaryEmail VARCHAR DEFAULT (null) ,accountNumber VARCHAR DEFAULT (null) ,periodStartDate VARCHAR DEFAULT (null) ,newBalance FLOAT,minPayment FLOAT,amountEnclosed FLOAT,cardTypeId  INTEGER DEFAULT 0,cardTypeName VARCHAR DEFAULT (null) ,cardNumber VARCHAR DEFAULT (null) ,cardHoldername VARCHAR DEFAULT (null) ,validThrough VARCHAR DEFAULT (null) ,invoiceNumber VARCHAR DEFAULT (null) ,numberOfLines  INTEGER DEFAULT 0,docData VARCHAR DEFAULT (null) ,createdDate VARCHAR DEFAULT (null) ,checksum VARCHAR DEFAULT (null) ,accountType VARCHAR DEFAULT (null) ,account VARCHAR DEFAULT (null) ,strCreatedDate VARCHAR DEFAULT (null) ,paymentsCredits FLOAT,paymentDate VARCHAR DEFAULT (null) ,monthlyCharges FLOAT,usageAndPurchaseCharges FLOAT,surAndOtherChargesCredits FLOAT,taxesAndFees FLOAT,totalAccountChargesAndCredits FLOAT,newPurchases FLOAT,newCashAdvance FLOAT,fees FLOAT,financeCharges FLOAT,creditLineCash FLOAT,creditLineTotal FLOAT,availableCreditCash FLOAT,availableCreditTotal FLOAT,latePaymentWarning VARCHAR DEFAULT (null) ,minPaymentWarning VARCHAR DEFAULT (null) ,currentMinAmountDue FLOAT,pastDue FLOAT,overlimitAmount FLOAT,avgDailyBalPurchases FLOAT,avgDailyBalCashAdv FLOAT,nominalAnnualPerRatePurchases FLOAT,nominalAnnualPerRateCashAdv FLOAT,monPeriodRatePurchases FLOAT,monPeriodRateCashAdv FLOAT,annualPerRatePurchases FLOAT,annualPerRateCashAdv FLOAT, isPDFDeleted BOOL DEFAULT 0, isDirty BOOL DEFAULT 0,showInCashFlow BOOL DEFAULT 1,showInStatements BOOL DEFAULT 1,showInBill BOOL DEFAULT 1,showInUnpaid BOOL DEFAULT 1, category VARCHAR DEFAULT (null))";        
        String userOfferProvidersFavorite = "CREATE TABLE if not exists UserOfferProvidersFavorite (userId INTEGER, providerId INTEGER, isFavorite BOOL DEFAULT 0, isDirty BOOL DEFAULT 0)";
        String userDocumentProvidersFavorite = "CREATE TABLE if not exists UserDocumentProvidersFavorite (userId INTEGER, providerId INTEGER, isFavorite BOOL DEFAULT 0, isDirty BOOL DEFAULT 0)";
        String settings = "CREATE TABLE Settings(lastSyncDateTime INTEGER DEFAULT 0, lastSyncDateTimeStr VARCHAR, userId Integer)";
        String tagColorSequence = "CREATE TABLE TagColorSequence(colorCode VARCHAR, sequenceId INTEGER)";
        String usercloudProviders = "CREATE TABLE if not exists UserCloudProviders (userId INTEGER, providerId INTEGER)";
        String userLibrary = "CREATE TABLE UserLibrary (userId INTEGER, libraryId INTEGER, file VARCHAR, cover VARCHAR, type VARCHAR, createdDate VARCHAR, modifiedDate VARCHAR, isActive BOOL DEFAULT 0, url VARCHAR, useUrl BOOL DEFAULT 0, isRead BOOL DEFAULT 0, isDirty BOOL DEFAULT 0)";
        
        String tagColorSequenceData = "insert into TagColorSequence (colorCode,sequenceId) values ('#EE1F25',1),('#C268A9',2),('#466DB4',3),('#BBBBBB',4),('#F3EC0C',5),('#4AB747',6),('#F58021',7),('#672A86',8)";
        
        String Category = "CREATE TABLE if not exists StatementsCategory (accountTypeId INTEGER , accountType VARCHAR, displayName VARCHAR, categoryType VARCHAR)";
        
        db.execSQL(statements);
        db.execSQL(cloudProvider);
        db.execSQL(cloudService);
        db.execSQL(clubOffer);
        db.execSQL(clubStatement);
        db.execSQL(contractTerminationFee);
        db.execSQL(contractTerminationFeesList);
        db.execSQL(dataPlanLines);
        db.execSQL(dataPlanProviderNames);
        db.execSQL(dataPlanTypes);
        db.execSQL(dataPlans);
        db.execSQL(offersService);
        db.execSQL(offersZipCodes);
        db.execSQL(phonesService);
        db.execSQL(statementsCost);
        db.execSQL(statementsSubAccounts);
        db.execSQL(subscriptionOffer);
        db.execSQL(telephoneStatementAccountPlan);
        db.execSQL(telphoneStatement);
        db.execSQL(users);
        db.execSQL(tags);       
        db.execSQL(offerProviders);
        db.execSQL(userofferProviders);
        db.execSQL(userSettings);
        db.execSQL(documentProviders);
        db.execSQL(userDocumentProviders);
        db.execSQL(state);
        db.execSQL(userOfferProvidersFavorite);
        db.execSQL(userDocumentProvidersFavorite);
        db.execSQL(settings);
        db.execSQL(tagColorSequence);
        db.execSQL(usercloudProviders);
        db.execSQL(userLibrary);
        db.execSQL(tagColorSequenceData);
        db.execSQL(Category);

    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Create tables again
        onCreate(db);
    }
 
   
 
}