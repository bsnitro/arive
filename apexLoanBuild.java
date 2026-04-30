public class LoanApplicationWebhookHandler {
    public class loanApplicationWrapper {
        public String loanId;
        public String externalId;
        public String loanNumber;
        public Decimal loanAmount;
        public String loanType;
        public String loanStatus;
        public String loanStatusReason;
        public String applicationDate;
        public String applicationMethod;
        public String transactionType;
        public Decimal downPayment;
        public Decimal housingRatio;
        public Decimal ltvRatio;
        public Decimal cltvRatio;
        public Decimal tltvRatio;
        public Decimal dtiRatio;
        public Boolean firstTimeHomeBuyer;
        public Boolean vaEligible;
        public Boolean hasVaLoan;
        public Boolean relationshipPricing;
        public Boolean includeClosingCosts;
        public String originationChannel;
        public Decimal wireAmountReceived;
        public String wireReceivedDate;
        public List<BorrowerPair> borrowerPairs;
        public List<Borrower> borrowers;
        public SubjectProperty subjectProperty;
        public List<Asset> assets;
        public List<Income> income;
        public List<KeyDate> keyDates;
        public List<Declaration> declarations;
        public List<TeamMember> teamMembers;
        public List<Milestone> milestones;
        public Metadata metadata;
        public List<Fee> fees;
        public List<CreditReport> creditReports;
        public List<Attribute> attributes;
        public Product product;
        public List<Note> notes;
        public List<Condition> conditions;
        public List<Document> documents;
        public List<Contact> contacts;
        public Integer lienPosition;
    }

    public class BorrowerPair {
        public String primaryBorrowerRef;
        public String secondaryBorrowerRef;
        public Integer order;
    }

    public class Borrower {
        public String borrowerId;
        public String firstName;
        public String lastName;
        public String phone;
        public String email;
        public String taxIdentifier;
        public String dateOfBirth;
        public String citizenship;
        public String maritalStatus;
        public String ethnicity;
        public String gender;
        public String race;
        public Integer creditScore;
        public List<Residence> residences;
        public List<Consent> consents;
        public Metadata metadata;
    }

    public class Residence {
        public String borrowerAddressId;
        public String street1;
        public String city;
        public String state;
        public String zipCode;
        public String rentOrOwn;
        public Decimal monthlyPayment;
        public String type;
        public String moveInDate;
        public String unitDesignatorType;
    }

    public class Consent {
        public String borrowerRef;
        public String type;
        public Boolean accepted;
        public String requestedDate;
        public String acceptedDate;
    }

    public class SubjectProperty {
        public String subjectPropertyId;
        public String street1;
        public String city;
        public String state;
        public String zipCode;
        public String county;
        public String propertyType;
        public Integer numberOfUnits;
        public Decimal estimatedValue;
        public Decimal purchasePrice;
        public Decimal taxes;
        public Decimal insurance;
        public Decimal hoaFees;
        public Decimal otherExpenses;
        public Boolean escrowTaxes;
        public Boolean escrowInsurance;
        public Decimal grossRentalIncome;
        public Decimal grossRentalIncomeFactor;
        public Decimal netRentalIncome;
        public String occupancyType;
        public Metadata metadata;
    }

    public class Asset {
        public String loanAssetId;
        public String type;
        public Decimal amount;
        public Boolean liquid;
        public String institution;
        public String assetDate;
        public List<String> borrowerRefs;
        public String account;
        public Boolean verified;
        public Metadata metadata;
    }

    public class Income {
        public String loanIncomeId;
        public String borrowerRef;
        public String type;
        public String description;
        public Boolean taxExempt;
        public Decimal amount;
        public Boolean primary;
        public Metadata metadata;
    }

    public class KeyDate {
        public String name;
        public String value;
    }

    public class Declaration {
        public String borrowerRef;
        public String completedDate;
        public Boolean outstandingJudgementsIndicator;
        public Boolean bankruptcyIndicator;
        public Boolean propertyForeclosedPastSevenYearsIndicator;
        public Boolean partyToLawsuitIndicator;
        public Boolean loanForeclosureOrJudgementIndicator;
        public Boolean presentlyDelinquentIndicator;
        public Boolean alimonyChildSupportObligationIndicator;
        public Boolean borrowedDownPaymentIndicator;
        public Boolean coMakerEndorserOfNoteIndicator;
        public Boolean usCitizenIndicator;
        public Boolean permanentResidentAlienIndicator;
        public Boolean intentToOccupyIndicator;
        public Boolean homeownerPastThreeYearsIndicator;
        public String priorPropertyUsage;
        public String priorPropertyTitle;
        public Boolean sellerIsFamilyOrAffiliateIndicator;
        public Decimal borrowedDownPaymentAmount;
        public Boolean otherMortgageLoanIndicator;
        public Boolean newCreditIndicator;
        public Boolean cleanEnergyLienIndicator;
        public Boolean conveyedTitleInLieuOfForeclosureIndicator;
        public Boolean preForeclosureShortSalePastSevenYearsIndicator;
        public String bankruptcyType;
        public Boolean militaryServiceIndicator;
    }

    public class TeamMember {
        public String email;
        public String role;
        public Boolean primary;
    }

    public class Milestone {
        public String name;
        public Boolean completed;
        public String completedDate;
        public Integer order;
    }

    public class MilestoneComparator implements System.Comparator<Milestone> {
        public Integer compare(Milestone m1, Milestone m2) {
            if (m1.order == null && m2.order == null) return 0;
            if (m1.order == null) return 1;
            if (m2.order == null) return -1;
            Integer orderDiff = m2.order - m1.order; // Descending order
            if (orderDiff != 0) return orderDiff;
            // Tie-break: same order -> prefer most recent completedDate (so it appears first after sort)
            Date d1 = convertISOStringToDate(m1.completedDate);
            Date d2 = convertISOStringToDate(m2.completedDate);
            if (d1 == null && d2 == null) return 0;
            if (d1 == null) return 1;
            if (d2 == null) return -1;
            // Most recent first: d2 > d1 means m2 should come before m1 -> compare(m1,m2) > 0 -> return 1
            if (d2 > d1) return 1;
            if (d2 < d1) return -1;
            return 0;
        }
    }

    public class Metadata {
        public List<MetadataItem> items;
    }

    public class MetadataItem {
        public String key;
        public String value;
    }

    public class Fee {
        public String loanFeeId;
        public String feeType;
        public String description;
        public String section;
        public Decimal amount;
        public Decimal borrowerPacAmount;
        public Decimal borrowerPocAmount;
        public Decimal sellerPacAmount;
        public Decimal sellerPocAmount;
        public Decimal othersPaidAmount;
        public String paidTo;
        public String paidBy;
        public Boolean borrowerCanShop;
        public Boolean borrowerDidShop;
        public Boolean borrowerDidSelect;
        public Boolean includeInApr;
        public Boolean canFinance;
        public Boolean refundable;
        public Boolean optional;
        public String feeBuilderArgs;
    }

    public class CreditReport {
        public String creditReportRef;
        public String documentRef;
        public List<String> borrowerRefs;
        public String timestamp;
        public String type;
        public String action;
        public String status;
        public String result;
        public List<String> bureaus;
    }

    public class Attribute {
        public String name;
        public String value;
    }

    public class Product {
        public String loanRef;
        public Integer lenderId;
        public String productCode;
        public String productName;
        public Integer productTypeId;
        public String mortgageType;
        public Integer loanTerm;
        public Integer lockPeriod;
        public Decimal price;
        public Decimal rate;
        public Decimal apr;
        public Decimal rebate;
        public Decimal discount;
        public Decimal principalAndInterest;
        public Decimal monthlyMortgageInsurance;
        public Decimal payment;
        public String timestamp;
        public Decimal upfrontFeeFactor;
        public Decimal mortgageInsuranceFactor;
        public String lenderName;
        public String compensation;
        public Boolean escrowTaxes;
        public Boolean escrowInsurance;
        public Integer amortizationTerm;
    }

    public class Note {
        public String loanNoteId;
        public String text;
        public Metadata metadata;
    }

    public class Condition {
        public String loanConditionId;
        public String borrowerRef;
        public String name;
        public String description;
        public String scope;
        public String type;
        public Boolean completed;
        public Boolean critical;
        public Metadata metadata;
    }

    // Internal normalized structure so condition processing is resilient to payload type drift.
    private class NormalizedConditionPayload {
        public String loanConditionId;
        public String borrowerRef;
        public String name;
        public String description;
        public String scope;
        public String type;
        public Boolean completed;
        public Boolean critical;
    }

    private static String normalizeToString(Object value) {
        if (value == null) {
            return null;
        }
        String strValue = String.valueOf(value);
        if (strValue == null) {
            return null;
        }
        strValue = strValue.trim();
        return String.isNotBlank(strValue) ? strValue : null;
    }

    private static Boolean normalizeToBoolean(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean)value;
        }
        String boolString = String.valueOf(value);
        if (String.isBlank(boolString)) {
            return null;
        }
        boolString = boolString.trim().toLowerCase();
        if (boolString == 'true') {
            return true;
        }
        if (boolString == 'false') {
            return false;
        }
        return null;
    }

    private static List<NormalizedConditionPayload> buildNormalizedConditions(List<Condition> typedConditions, String loanDataJson) {
        List<NormalizedConditionPayload> normalizedConditions = new List<NormalizedConditionPayload>();
        Integer typedWithIdCount = 0;

        if (typedConditions != null) {
            for (Condition typedCondition : typedConditions) {
                NormalizedConditionPayload normalized = new NormalizedConditionPayload();
                normalized.loanConditionId = normalizeToString(typedCondition.loanConditionId);
                normalized.borrowerRef = normalizeToString(typedCondition.borrowerRef);
                normalized.name = typedCondition.name;
                normalized.description = typedCondition.description;
                normalized.scope = typedCondition.scope;
                normalized.type = typedCondition.type;
                normalized.completed = typedCondition.completed;
                normalized.critical = typedCondition.critical;
                if (String.isNotBlank(normalized.loanConditionId)) {
                    typedWithIdCount++;
                }
                normalizedConditions.add(normalized);
            }
        }

        // If typed deserialization produced no usable IDs, fall back to raw JSON parsing.
        if ((typedConditions == null || typedConditions.isEmpty() || typedWithIdCount == 0) && String.isNotBlank(loanDataJson)) {
            List<NormalizedConditionPayload> rawNormalized = new List<NormalizedConditionPayload>();
            Map<String, NormalizedConditionPayload> dedupByKey = new Map<String, NormalizedConditionPayload>();
            Map<String, Object> root = (Map<String, Object>)JSON.deserializeUntyped(loanDataJson);
            if (root != null && root.containsKey('conditions') && root.get('conditions') != null) {
                List<Object> rawConditions = (List<Object>)root.get('conditions');
                for (Object rawConditionObj : rawConditions) {
                    if (!(rawConditionObj instanceof Map<String, Object>)) {
                        continue;
                    }
                    Map<String, Object> rawCondition = (Map<String, Object>)rawConditionObj;
                    String rawLoanConditionId = normalizeToString(rawCondition.get('loanConditionId'));
                    if (String.isBlank(rawLoanConditionId)) {
                        rawLoanConditionId = normalizeToString(rawCondition.get('loanConditionID'));
                    }
                    if (String.isBlank(rawLoanConditionId)) {
                        rawLoanConditionId = normalizeToString(rawCondition.get('conditionId'));
                    }

                    if (String.isBlank(rawLoanConditionId)) {
                        continue;
                    }

                    String rawBorrowerRef = normalizeToString(rawCondition.get('borrowerRef'));
                    String key = rawLoanConditionId + '_' + (String.isNotBlank(rawBorrowerRef) ? rawBorrowerRef : '');

                    NormalizedConditionPayload normalized = new NormalizedConditionPayload();
                    normalized.loanConditionId = rawLoanConditionId;
                    normalized.borrowerRef = rawBorrowerRef;
                    normalized.name = normalizeToString(rawCondition.get('name'));
                    normalized.description = normalizeToString(rawCondition.get('description'));
                    normalized.scope = normalizeToString(rawCondition.get('scope'));
                    normalized.type = normalizeToString(rawCondition.get('type'));
                    normalized.completed = normalizeToBoolean(rawCondition.get('completed'));
                    normalized.critical = normalizeToBoolean(rawCondition.get('critical'));

                    dedupByKey.put(key, normalized);
                }
            }
            rawNormalized.addAll(dedupByKey.values());
            return rawNormalized;
        }

        return normalizedConditions;
    }

    public class Document {
        public String documentId;
        public String filename;
        public Integer size;
        public String type;
        public String description;
        public String safeDescription;
        public String summary;
    }

    public class Contact {
        public String contactRef;
        public String role;
        public String contactType;
        public String companyName;
        public String firstName;
        public String lastName;
        public String email;
        public String phone;
        public String street1;
        public String street2;
        public String city;
        public String state;
        public String zipCode;
        public String country;
    }

    // Webhook event wrapper class
    public class WebhookEvent {
        public String event;
        public String contextId;
        public String objectId;
        public String timestamp;
        public Map<String, Object> data;
    }

    public static void handleWebhook(String payloadJson) {
        try {
            System.debug('=== WEBHOOK RECEIVED ===');
            System.debug('Timestamp: ' + System.now());
            System.debug('Webhook Body: ' + payloadJson);
            
            // Deserialize the webhook event payload
            WebhookEvent webhookEvent = (WebhookEvent)JSON.deserialize(payloadJson, WebhookEvent.class);
            
            // Debug the incoming webhook event
            System.debug('=== WEBHOOK EVENT DETAILS ===');
            System.debug('Event Type: ' + webhookEvent.event);
            System.debug('Context ID: ' + webhookEvent.contextId);
            System.debug('Object ID: ' + webhookEvent.objectId);
            System.debug('Webhook Timestamp: ' + webhookEvent.timestamp);
            
            // Debug the data payload if present
            if (webhookEvent.data != null && !webhookEvent.data.isEmpty()) {
                System.debug('Webhook Data Keys: ' + String.valueOf(webhookEvent.data.keySet()));
                for (String key : webhookEvent.data.keySet()) {
                    System.debug('Data[' + key + ']: ' + String.valueOf(webhookEvent.data.get(key)));
                }
            } else {
                System.debug('Webhook Data: empty or null');
            }
            
            // Extract loan ID from contextId (format: "LoanId:286")
            String loanId = null;
            if (webhookEvent.contextId != null && webhookEvent.contextId.startsWith('LoanId:')) {
                loanId = webhookEvent.contextId.substringAfter('LoanId:');
                System.debug('Extracted loanId: ' + loanId);
            }
            
            // Enqueue the processing job to handle row locking gracefully
            if (loanId != null) {
                System.debug('Enqueueing Queueable job for loanId: ' + loanId);
                System.enqueueJob(new LoanWebhookQueueable(loanId));
                System.debug('Queueable job enqueued successfully for loanId: ' + loanId);
            } else {
                System.debug('No loanId found in webhook payload, cannot process');
            }
            
        } catch (Exception e) {
            System.debug('Error processing webhook: ' + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Queueable class to process webhook data asynchronously
     * This helps prevent UNABLE_TO_LOCK_ROW errors by:
     * 1. Serializing processing for the same loan
     * 2. Allowing retry logic for row lock contention
     * 3. Offloading processing from the webhook response thread
     */
    public class LoanWebhookQueueable implements Queueable, Database.AllowsCallouts {
        private String loanId;
        private Integer retryCount;
        private final Integer MAX_DML_RETRIES = 3;
        
        public LoanWebhookQueueable(String loanId) {
            this.loanId = loanId;
            this.retryCount = 0;
        }
        
        public LoanWebhookQueueable(String loanId, Integer retryCount) {
            this.loanId = loanId;
            this.retryCount = retryCount;
        }
        
        public void execute(QueueableContext context) {
            System.debug('=== QUEUEABLE EXECUTION START ===');
            System.debug('Processing loanId: ' + loanId + ' (attempt ' + (retryCount + 1) + ')');
            
            try {
                // Call the existing getSonarLoanData logic but use the non-future version
                getSonarLoanDataSync(loanId);
                System.debug('=== QUEUEABLE EXECUTION COMPLETE ===');
            } catch (Exception e) {
                System.debug('Error in Queueable execution: ' + e.getMessage());
                System.debug('Stack trace: ' + e.getStackTraceString());
                
                // Check if this is a row lock error and we can retry
                if (isRowLockError(e) && retryCount < MAX_DML_RETRIES) {
                    System.debug('Row lock error detected, re-enqueueing job (retry ' + (retryCount + 1) + ' of ' + MAX_DML_RETRIES + ')');
                    // Re-enqueue with incremented retry count
                    System.enqueueJob(new LoanWebhookQueueable(loanId, retryCount + 1));
                } else if (retryCount >= MAX_DML_RETRIES) {
                    System.debug('Max retries reached for loanId: ' + loanId + '. Error: ' + e.getMessage());
                    // Log the failure but don't throw - we've exhausted retries
                    logProcessingFailure(loanId, e.getMessage(), retryCount);
                } else {
                    // Non-row-lock error, log and don't retry
                    System.debug('Non-retryable error for loanId: ' + loanId + '. Error: ' + e.getMessage());
                    throw e;
                }
            }
        }
        
        /**
         * Check if the exception is a row lock error
         */
        private Boolean isRowLockError(Exception e) {
            String message = e.getMessage();
            return message != null && (
                message.contains('UNABLE_TO_LOCK_ROW') || 
                message.contains('unable to obtain exclusive access')
            );
        }
        
        /**
         * Log processing failure for monitoring
         */
        private void logProcessingFailure(String loanId, String errorMessage, Integer attempts) {
            System.debug('=== PROCESSING FAILURE LOG ===');
            System.debug('LoanId: ' + loanId);
            System.debug('Error: ' + errorMessage);
            System.debug('Attempts: ' + (attempts + 1));
            System.debug('Timestamp: ' + System.now());
            // Could be extended to create a custom object record for tracking failures
        }
    }
    
    /**
     * Synchronous version of getSonarLoanData for use by Queueable
     * Contains the same logic as the @future method but runs synchronously
     */
    public static void getSonarLoanDataSync(String loanId) {
        Integer maxRetries = 5;
        Integer retryCount = 0;
        Integer timeoutMs = 120000;
        
        if (isSonarCircuitOpen()) {
            System.debug('Sonar circuit breaker is OPEN - skipping callout for loanId: ' + loanId);
            throw new CalloutException('Sonar API is currently unavailable due to widespread issues. Please try again later.');
        }
        
        while (retryCount < maxRetries) {
            try {
                Sonar_API__c apiSettings = Sonar_API__c.getInstance();
                Http http = new Http();
                HttpRequest request = new HttpRequest();
                
                String endPointAPI = apiSettings.WebServices_URL__c + '/api/v1/loans/' + loanId;
                request.setEndpoint(endPointAPI);
                request.setHeader('x-api-key', apiSettings.Client_Secret__c);
                request.setHeader('Content-Type', 'application/json');
                request.setMethod('GET');
                request.setTimeout(timeoutMs);
                
                System.debug('Making request to Sonar API (attempt ' + (retryCount + 1) + ' of ' + maxRetries + '): ' + endPointAPI);
                
                HttpResponse response = http.send(request);
                System.debug('Response Status Code: ' + response.getStatusCode());
                
                if (response.getStatusCode() == 200) {
                    System.debug('Loan Data received from Sonar');
                    String productDataJson = getProductData(loanId);
                    String transactionDataJson = getTransactionData(loanId);
                    processLoanDataWithRetry(response.getBody(), productDataJson, transactionDataJson);
                    recordSonarSuccess();
                    return;
                } else {
                    if (response.getStatusCode() == 404) {
                        if (retryCount < 2) {
                            throw new CalloutException('Loan not found - retrying due to potential Sonar latency');
                        } else {
                            throw new CalloutException('Failed to get loan data from Sonar. Status: ' + response.getStatusCode());
                        }
                    } else if (response.getStatusCode() >= 400 && response.getStatusCode() < 500) {
                        throw new CalloutException('Failed to get loan data from Sonar. Status: ' + response.getStatusCode());
                    }
                    throw new CalloutException('Server error from Sonar. Status: ' + response.getStatusCode());
                }
                
            } catch (CalloutException e) {
                retryCount++;
                System.debug('Callout attempt ' + retryCount + ' failed: ' + e.getMessage());
                
                if (retryCount >= maxRetries) {
                    recordSonarFailure();
                    throw new CalloutException('Failed to get loan data from Sonar after ' + maxRetries + ' attempts. Last error: ' + e.getMessage());
                }
                
                Integer baseDelay = 1000;
                Integer exponentialDelay = baseDelay * (Integer)Math.pow(2, retryCount - 1);
                Integer jitter = (Integer)(Math.random() * 1000);
                Integer waitTimeMs = Math.min(exponentialDelay + jitter, 30000);
                
                Long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < waitTimeMs) {
                    // Simple delay loop
                }
            } catch (Exception e) {
                throw e;
            }
        }
    }
    
    /**
     * Process loan data with retry logic for DML operations
     * Wraps processLoanData with row lock retry handling
     */
    private static void processLoanDataWithRetry(String loanDataJson, String productDataJson, String transactionDataJson) {
        Integer maxDmlRetries = 3;
        Integer dmlRetryCount = 0;
        
        while (dmlRetryCount < maxDmlRetries) {
            try {
                processLoanData(loanDataJson, productDataJson, transactionDataJson);
                return; // Success
            } catch (DmlException e) {
                String message = e.getMessage();
                if ((message.contains('UNABLE_TO_LOCK_ROW') || message.contains('unable to obtain exclusive access')) 
                    && dmlRetryCount < maxDmlRetries - 1) {
                    dmlRetryCount++;
                    System.debug('Row lock error on DML, retrying (attempt ' + (dmlRetryCount + 1) + ' of ' + maxDmlRetries + ')');
                    
                    // Exponential backoff: 500ms, 1000ms, 2000ms
                    Integer waitTimeMs = 500 * (Integer)Math.pow(2, dmlRetryCount - 1);
                    Long startTime = System.currentTimeMillis();
                    while (System.currentTimeMillis() - startTime < waitTimeMs) {
                        // Wait
                    }
                } else {
                    throw e; // Non-retryable or max retries reached
                }
            }
        }
    }

    // METHOD NAME: getSonarLoanData
    // DESCRIPTION: Method to retrieve loan data from Sonar using API key authentication
    // Parameters ------------------------------------------------------------------------------------------
    // loanId : Sonar Loan ID to retrieve data for
    @future(callout=true)
    public static void getSonarLoanData(String loanId) {
        Integer maxRetries = 5; // Increased from 3 to 5 for better resilience
        Integer retryCount = 0;
        Integer timeoutMs = 120000; // 2 minutes timeout
        
        // Circuit breaker: Check if Sonar is experiencing widespread issues
        if (isSonarCircuitOpen()) {
            System.debug('Sonar circuit breaker is OPEN - skipping callout for loanId: ' + loanId);
            throw new CalloutException('Sonar API is currently unavailable due to widespread issues. Please try again later.');
        }
        
        while (retryCount < maxRetries) {
            try {
                // Setup Custom Setting Object
                Sonar_API__c apiSettings = Sonar_API__c.getInstance();
                
                // Create the HTTP request
                Http http = new Http();
                HttpRequest request = new HttpRequest();
                
                // Set the Endpoint URL
                String endPointAPI = apiSettings.WebServices_URL__c + '/api/v1/loans/' + loanId;
                request.setEndpoint(endPointAPI);
                
                // Set the authorization header with API key
                request.setHeader('x-api-key', apiSettings.Client_Secret__c);
                request.setHeader('Content-Type', 'application/json');
                request.setMethod('GET');
                
                // Set timeout
                request.setTimeout(timeoutMs);
                
                System.debug('Making request to Sonar API (attempt ' + (retryCount + 1) + ' of ' + maxRetries + '): ' + endPointAPI);
                System.debug('Timeout set to: ' + timeoutMs + 'ms');
                
                // Send the request
                HttpResponse response = http.send(request);
                System.debug('Response Status: ' + response.getStatus());
                System.debug('Response Status Code: ' + response.getStatusCode());
                
                // Check if the request is successful
                if (response.getStatusCode() == 200) {
                    System.debug('Loan Data received from Sonar: ' + response.getBody());
                    
                    // Make additional callout to get product data
                    System.debug('=== ABOUT TO MAKE PRODUCT DATA API CALL ===');
                    String productDataJson = getProductData(loanId);
                    System.debug('=== PRODUCT DATA API CALL COMPLETED ===');
                    String transactionDataJson = getTransactionData(loanId);
                    System.debug('=== TRANSACTION DATA API CALL COMPLETED ===');
                    System.debug('Product data JSON received: ' + (productDataJson != null ? productDataJson.substring(0, Math.min(200, productDataJson.length())) + '...' : 'null'));
                    
                    // Process loan data, product data, and transaction data (lien position)
                    processLoanData(response.getBody(), productDataJson, transactionDataJson);
                    
                    // Record successful call for circuit breaker
                    recordSonarSuccess();
                    return; // Success - exit the retry loop
                } else {
                    System.debug('Error Status: ' + response.getStatus());
                    System.debug('Error Status Code: ' + response.getStatusCode());
                    System.debug('Error Body: ' + response.getBody());
                    
                // Handle different error types with different retry strategies
                if (response.getStatusCode() == 404) {
                    // 404 - Loan not found, but could be due to Sonar latency
                    // Retry a few times in case it's a temporary issue
                    if (retryCount < 2) {
                        System.debug('404 error - retrying in case of Sonar latency (attempt ' + (retryCount + 1) + ')');
                        throw new CalloutException('Loan not found - retrying due to potential Sonar latency. Status: ' + response.getStatusCode());
                    } else {
                        throw new CalloutException('Failed to get loan data from Sonar. Status: ' + response.getStatusCode() + ' - ' + response.getBody());
                    }
                } else if (response.getStatusCode() >= 400 && response.getStatusCode() < 500) {
                    // Other 4xx errors - likely permanent, don't retry
                    throw new CalloutException('Failed to get loan data from Sonar. Status: ' + response.getStatusCode() + ' - ' + response.getBody());
                }
                
                // For server errors (5xx), continue to retry
                throw new CalloutException('Server error from Sonar. Status: ' + response.getStatusCode());
                }
                
            } catch (CalloutException e) {
                retryCount++;
                System.debug('Callout attempt ' + retryCount + ' failed: ' + e.getMessage());
                
                
                if (retryCount >= maxRetries) {
                    System.debug('All retry attempts failed for loanId: ' + loanId);
                    
                    // Log RLA record information for debugging
                    try {
                        List<ResidentialLoanApplication__c> rlaRecords = [
                            SELECT Id, SonarLoanID__c, Sonar_GUID__c, Status__c, milestoneCurrentName__c, 
                                   BorrowerFirstNamec__c, BorrowerLastNamec__c, Loan_Amount__c
                            FROM ResidentialLoanApplication__c 
                            WHERE SonarLoanID__c = :loanId 
                            LIMIT 1
                        ];
                        
                        if (!rlaRecords.isEmpty()) {
                            ResidentialLoanApplication__c rla = rlaRecords[0];
                            System.debug('RLA Record Details for failed loanId ' + loanId + ':');
                            System.debug('  - RLA ID: ' + rla.Id);
                            System.debug('  - Sonar_GUID__c: ' + rla.Sonar_GUID__c);
                            System.debug('  - Status__c: ' + rla.Status__c);
                            System.debug('  - milestoneCurrentName__c: ' + rla.milestoneCurrentName__c);
                            System.debug('  - Borrower: ' + rla.BorrowerFirstNamec__c + ' ' + rla.BorrowerLastNamec__c);
                            System.debug('  - Loan Amount: ' + rla.Loan_Amount__c);
                        } else {
                            System.debug('No RLA record found for loanId: ' + loanId);
                        }
                    } catch (Exception debugException) {
                        System.debug('Error retrieving RLA record for debugging: ' + debugException.getMessage());
                    }
                    
                    // Record failure for circuit breaker
                    recordSonarFailure();
                    throw new CalloutException('Failed to get loan data from Sonar after ' + maxRetries + ' attempts. Last error: ' + e.getMessage());
                }
                
                // Wait before retrying (exponential backoff with jitter)
                Integer baseDelay = 1000; // 1 second base
                Integer exponentialDelay = baseDelay * (Integer)Math.pow(2, retryCount - 1); // 1s, 2s, 4s, 8s, 16s
                Integer jitter = (Integer)(Math.random() * 1000); // Add up to 1 second of jitter
                Integer waitTimeMs = exponentialDelay + jitter;
                
                // Cap the maximum wait time at 30 seconds
                if (waitTimeMs > 30000) {
                    waitTimeMs = 30000;
                }
                
                System.debug('Waiting ' + waitTimeMs + 'ms before retry (exponential backoff with jitter)...');
                
                try {
                    // Use a simple delay mechanism
                    Long startTime = System.currentTimeMillis();
                    while (System.currentTimeMillis() - startTime < waitTimeMs) {
                        // Simple delay loop
                    }
                } catch (Exception delayException) {
                    System.debug('Error during retry delay: ' + delayException.getMessage());
                }
                
            } catch (Exception e) {
                System.debug('Non-callout error making request to Sonar: ' + e.getMessage());
                throw e; // Don't retry on non-callout errors
            }
        }
    }

    // METHOD NAME: processLoanData
    // DESCRIPTION: Method to process the loan data received from Sonar
    // Parameters ------------------------------------------------------------------------------------------
    // loanDataJson : JSON string containing the loan data from Sonar
    // productDataJson : JSON string containing the product data from Sonar
    // transactionDataJson : JSON from GET /loans/{id}/transaction (e.g. lien position); may be null or '{}'
    private static void processLoanData(String loanDataJson, String productDataJson, String transactionDataJson) {
        try {
            // Deserialize the Sonar API response
            loanApplicationWrapper app = (loanApplicationWrapper)JSON.deserialize(loanDataJson, loanApplicationWrapper.class);
            
            // Early check for fee data availability
            System.debug('=== EARLY FEE DATA CHECK ===');
            System.debug('Loan data contains fees: ' + (app.fees != null && !app.fees.isEmpty()));
            if (app.fees != null && !app.fees.isEmpty()) {
                System.debug('Number of fees in payload: ' + app.fees.size());
                
                // Count unique loanFeeIds and feeTypes for analysis
                Set<String> uniqueFeeIds = new Set<String>();
                Map<String, Integer> feeTypeCounts = new Map<String, Integer>();
                
                for (Integer i = 0; i < app.fees.size(); i++) {
                    Fee fee = app.fees[i];
                    System.debug('Fee #' + (i+1) + ': ' + fee.feeType + ' = $' + fee.amount + ' (loanFeeId: ' + fee.loanFeeId + ')');
                    
                    // Track unique fee IDs
                    if (fee.loanFeeId != null) {
                        uniqueFeeIds.add(String.valueOf(fee.loanFeeId));
                    }
                    
                    // Count fee types
                    if (fee.feeType != null) {
                        if (feeTypeCounts.containsKey(fee.feeType)) {
                            feeTypeCounts.put(fee.feeType, feeTypeCounts.get(fee.feeType) + 1);
                        } else {
                            feeTypeCounts.put(fee.feeType, 1);
                        }
                    }
                }
                
                System.debug('Unique loanFeeIds: ' + uniqueFeeIds.size());
                System.debug('Fee type breakdown:');
                for (String feeType : feeTypeCounts.keySet()) {
                    System.debug('  ' + feeType + ': ' + feeTypeCounts.get(feeType) + ' record(s)');
                }
            } else {
                System.debug('WARNING: No fee data in this webhook payload');
            }
            System.debug('=== END EARLY FEE DATA CHECK ===');
            
            // Debug product information from the main loan response
            System.debug('=== PRODUCT INFORMATION FROM MAIN LOAN RESPONSE ===');
            System.debug('app.product: ' + (app.product != null ? 'not null' : 'null'));
            if (app.product != null) {
                System.debug('app.product.lenderName: ' + app.product.lenderName);
                System.debug('app.product.productCode: ' + app.product.productCode);
                System.debug('app.product.mortgageType: ' + app.product.mortgageType);
                System.debug('app.product.rate: ' + app.product.rate);
                System.debug('app.product.apr: ' + app.product.apr);
            } else {
                System.debug('No product information found in main loan response');
            }
            System.debug('=== END PRODUCT INFORMATION ===');
            
            // Deserialize the product data from separate API call
            Product productData = null;
            System.debug('=== PRODUCT DATA DESERIALIZATION START ===');
            System.debug('productDataJson value: "' + productDataJson + '"');
            System.debug('productDataJson is null: ' + (productDataJson == null));
            System.debug('productDataJson is empty: ' + (productDataJson == '{}'));
            System.debug('productDataJson length: ' + (productDataJson != null ? String.valueOf(productDataJson.length()) : 'null'));
            
            if (productDataJson != null && productDataJson != '{}') {
                try {
                    System.debug('Raw product data JSON: ' + productDataJson);
                    System.debug('About to deserialize product data...');
                    
                    // Try to parse as Map first to see the structure
                    Map<String, Object> productMap = (Map<String, Object>)JSON.deserializeUntyped(productDataJson);
                    System.debug('Product data as Map: ' + productMap);
                    System.debug('Product data Map keys: ' + (productMap != null ? String.valueOf(productMap.keySet()) : 'null'));
                    if (productMap != null && productMap.containsKey('lenderName')) {
                        System.debug('lenderName in Map: ' + productMap.get('lenderName'));
                    }
                    
                    productData = (Product)JSON.deserialize(productDataJson, Product.class);
                    System.debug('Product data deserialized successfully');
                    System.debug('Product data object: ' + productData);
                    System.debug('Product data - loanRef: ' + (productData != null ? productData.loanRef : 'null'));
                    System.debug('Product data - lenderName: ' + (productData != null ? productData.lenderName : 'null'));
                    System.debug('Product data - productCode: ' + (productData != null ? productData.productCode : 'null'));
                    System.debug('Product data - mortgageType: ' + (productData != null ? productData.mortgageType : 'null'));
                    
                    // Additional debugging to check if lenderName is actually set
                    if (productData != null) {
                        System.debug('Product data lenderName field value: "' + productData.lenderName + '"');
                        if (productData.lenderName != null) {
                            System.debug('Product data lenderName field length: ' + String.valueOf(productData.lenderName.length()));
                        } else {
                            System.debug('Product data lenderName field length: null');
                        }
                    }
                } catch (Exception e) {
                    System.debug('Error deserializing product data: ' + e.getMessage());
                    System.debug('Error stack trace: ' + e.getStackTraceString());
                    System.debug('Error type: ' + e.getTypeName());
                    productData = null;
                }
            } else {
                System.debug('No product data available - productDataJson is null or empty');
            }
            System.debug('=== PRODUCT DATA DESERIALIZATION END ===');
            System.debug('Final productData value: ' + (productData != null ? 'not null' : 'null'));
            
            // Debug the Sonar API response identifiers
            System.debug('Sonar API response - externalId: ' + app.externalId);
            System.debug('Sonar API response - loanId: ' + app.loanId);
            System.debug('Sonar API response - loanNumber: ' + app.loanNumber);
            System.debug('Sonar API response - loanStatus: ' + app.loanStatus);
            System.debug('Sonar API response - loanStatusReason: ' + app.loanStatusReason);
            
            // Find existing record by Sonar GUID
            ResidentialLoanApplication__c rla;
            
            // Create the Sonar GUID by concatenating externalId and loanId with underscore
            String sonarGuid = null;
            if (app.externalId != null && app.loanId != null) {
                sonarGuid = app.externalId + '_' + app.loanId;
            }
            
            System.debug('Looking for record with Sonar_GUID__c: ' + sonarGuid);
            
            try {
                // Query for existing record using Sonar_GUID__c as the primary key
                List<ResidentialLoanApplication__c> existingRecords = new List<ResidentialLoanApplication__c>();
                
                if (sonarGuid != null) {
                    // Use FOR UPDATE to lock the row and prevent concurrent updates
                    String query = 'SELECT Id, Application_Date__c, ApplicationExtIdentifier__c, SonarLoanID__c, Sonar_GUID__c, milestoneCurrentName__c, Status__c, Date_File_Started__c, ' +
                                 'Proposed_Payment_First_Mortgage__c, Proposed_Payment_Mortgage_Insurance__c, Proposed_Payment_Property_Taxes__c, ' +
                                 'Proposed_Payment_Other_Expenses__c, Proposed_Payment_Homeowners_Insurance__c, Proposed_Payment_HOA_Dues__c, ' +
                                 'Total_Proposed_Monthly_Payment__c, Property_Address__c, Processor_Email__c, Lender_Name__c, Borrower__c, Co_Borrower__c ' +
                                 'FROM ResidentialLoanApplication__c ' +
                                 'WHERE Sonar_GUID__c = :sonarGuid ' +
                                 'FOR UPDATE';
                    
                    System.debug('Executing query with FOR UPDATE: ' + query);
                    System.debug('With values - sonarGuid: ' + sonarGuid);
                    existingRecords = Database.query(query);
                }
                
                // If not found by Sonar_GUID__c, try by individual fields as fallback
                if (existingRecords.isEmpty() && app != null && app.externalId != null && app.loanId != null) {
                    System.debug('Executing fallback query by individual fields with FOR UPDATE');
                    System.debug('With values - externalId: ' + app.externalId + ', loanId: ' + app.loanId);
                    existingRecords = [
                        SELECT Id, Application_Date__c, ApplicationExtIdentifier__c, SonarLoanID__c, Sonar_GUID__c, milestoneCurrentName__c, Status__c, Date_File_Started__c,
                               Proposed_Payment_First_Mortgage__c, Proposed_Payment_Mortgage_Insurance__c, Proposed_Payment_Property_Taxes__c,
                               Proposed_Payment_Other_Expenses__c, Proposed_Payment_Homeowners_Insurance__c, Proposed_Payment_HOA_Dues__c,
                               Total_Proposed_Monthly_Payment__c, Property_Address__c, Processor_Email__c, Lender_Name__c, Borrower__c, Co_Borrower__c
                        FROM ResidentialLoanApplication__c 
                        WHERE ApplicationExtIdentifier__c = :app.externalId 
                        AND SonarLoanID__c = :app.loanId
                        FOR UPDATE
                    ];
                }
                
                // Third level: If still not found, try by SonarLoanID__c only
                if (existingRecords.isEmpty() && app != null && app.loanId != null) {
                    System.debug('Executing third level fallback query by SonarLoanID__c only with FOR UPDATE');
                    System.debug('With values - loanId: ' + app.loanId);
                    existingRecords = [
                        SELECT Id, Application_Date__c, ApplicationExtIdentifier__c, SonarLoanID__c, Sonar_GUID__c, milestoneCurrentName__c, Status__c, Date_File_Started__c,
                               Proposed_Payment_First_Mortgage__c, Proposed_Payment_Mortgage_Insurance__c, Proposed_Payment_Property_Taxes__c,
                               Proposed_Payment_Other_Expenses__c, Proposed_Payment_Homeowners_Insurance__c, Proposed_Payment_HOA_Dues__c,
                               Total_Proposed_Monthly_Payment__c, Property_Address__c, Processor_Email__c, Lender_Name__c, Borrower__c, Co_Borrower__c
                        FROM ResidentialLoanApplication__c 
                        WHERE SonarLoanID__c = :app.loanId
                        FOR UPDATE
                    ];
                }
                
                System.debug('Found existing records: ' + existingRecords.size());
                for(ResidentialLoanApplication__c record : existingRecords) {
                    System.debug('Found record - ID: ' + record.Id + 
                               ', ApplicationExtIdentifier__c: ' + record.ApplicationExtIdentifier__c + 
                               ', SonarLoanID__c: ' + record.SonarLoanID__c + 
                               ', Sonar_GUID__c: ' + record.Sonar_GUID__c +
                               ', milestoneCurrentName__c: ' + record.milestoneCurrentName__c);
                }
                
                if (!existingRecords.isEmpty()) {
                    rla = existingRecords[0];
                    System.debug('Using existing record: ' + rla.Id + ', current Status__c: ' + rla.Status__c + ', current milestoneCurrentName__c: ' + rla.milestoneCurrentName__c);
                    
                    // If the existing record doesn't have Sonar_GUID__c populated, set it
                    if (rla.Sonar_GUID__c == null && sonarGuid != null) {
                        rla.Sonar_GUID__c = sonarGuid;
                        System.debug('Setting Sonar_GUID__c on existing record: ' + sonarGuid);
                    }
                }
                
                if (rla == null) {
                    rla = new ResidentialLoanApplication__c();
                    System.debug('No existing record found, creating new record');
                }
                
                // Map loan-level fields (only set identifiers for new records)
                if (rla.Id == null) {
                    rla.ApplicationExtIdentifier__c = app.externalId;
                    rla.SonarLoanID__c = app.loanId;
                    rla.Sonar_GUID__c = sonarGuid;
                }
                
                // Set default values early in the process
                rla.Compensation_Type__c = 'Lender Paid';
                rla.Broker_Compensation_Percentage__c = 2.375; // 2.375% - field expects percentage value
                rla.Lock_Status__c = 'Not Locked';
                rla.Channelc__c = 'Brokered';
                rla.loanAmortizationType__c = 'Fixed Rate';
                rla.Occupancy_Type__c = 'Primary';
                
                // Map all other fields
                // Check for LenderLoanNumber in attributes first, fall back to loanNumber
                String lenderLoanNumber = null;
                String titleCompanyFileNumber = null;
                if (app.attributes != null && !app.attributes.isEmpty()) {
                    for (Attribute attr : app.attributes) {
                        if (attr == null) { continue; }
                        if (attr.name == 'LenderLoanNumber' && attr.value != null && attr.value.trim() != '') {
                            lenderLoanNumber = attr.value.trim();
                        } else if (attr.name == 'TitleCompanyFileNumber' && attr.value != null && attr.value.trim() != '') {
                            titleCompanyFileNumber = attr.value.trim();
                        }
                    }
                }
                
                // Use LenderLoanNumber if available, otherwise use app.loanNumber
                rla.Loan_Number__c = lenderLoanNumber != null ? lenderLoanNumber : app.loanNumber;
                System.debug('Setting Loan_Number__c to: ' + rla.Loan_Number__c + ' (LenderLoanNumber: ' + lenderLoanNumber + ', app.loanNumber: ' + app.loanNumber + ')');
                
                // Map TitleCompanyFileNumber if available
                if (titleCompanyFileNumber != null) {
                    rla.Title_Company_File_Number__c = titleCompanyFileNumber;
                    System.debug('Setting Title_Company_File_Number__c to: ' + titleCompanyFileNumber);
                } else {
                    rla.Title_Company_File_Number__c = null;
                    System.debug('No TitleCompanyFileNumber attribute found, setting Title_Company_File_Number__c to null');
                }
                
                // Map lienPosition to Lien_Position_Number__c and Lien_Position__c
                // Prefer GET /loans/{id}/transaction when available; otherwise use main loan payload
                Integer lienPosition = getLienPositionFromTransactionJson(transactionDataJson);
                if (lienPosition == null && app.lienPosition != null) {
                    lienPosition = app.lienPosition;
                }
                if (lienPosition != null) {
                    rla.Lien_Position_Number__c = String.valueOf(lienPosition);
                    if (lienPosition == 1) {
                        rla.Lien_Position__c = 'FirstLien';
                        System.debug('Set Lien_Position__c to FirstLien (lienPosition=1)');
                    } else if (lienPosition == 2) {
                        rla.Lien_Position__c = 'SecondLien';
                        System.debug('Set Lien_Position__c to SecondLien (lienPosition=2)');
                    } else {
                        rla.Lien_Position__c = null;
                    }
                    System.debug('Set Lien_Position_Number__c to: ' + rla.Lien_Position_Number__c);
                }
                
                // Set Total_Loan_Amount__c to the original loan amount
                rla.Total_Loan_Amount__c = app.loanAmount;
                
                // Calculate Loan_Amount__c by checking for FHA/VA fees
                Decimal loanAmountAfterFees = app.loanAmount;
                Set<String> processedFeeTypes = new Set<String>(); // Track processed fee types to avoid duplicates
                if (app.fees != null && !app.fees.isEmpty()) {
                    for (Fee fee : app.fees) {
                        if ((fee.feeType == 'FhaUpfrontMortgageInsurancePremium' || fee.feeType == 'VaFundingFee') && 
                            !processedFeeTypes.contains(fee.feeType)) {
                            // Calculate fee amount - use fee.amount if available, otherwise sum all individual amount fields
                            Decimal feeAmount = fee.amount;
                            if (feeAmount == null) {
                                feeAmount = 0.0;
                                if (fee.borrowerPacAmount != null) feeAmount += fee.borrowerPacAmount;
                                if (fee.borrowerPocAmount != null) feeAmount += fee.borrowerPocAmount;
                                if (fee.sellerPacAmount != null) feeAmount += fee.sellerPacAmount;
                                if (fee.sellerPocAmount != null) feeAmount += fee.sellerPocAmount;
                                if (fee.othersPaidAmount != null) feeAmount += fee.othersPaidAmount;
                            }
                            if (feeAmount != null && feeAmount != 0) {
                                loanAmountAfterFees = loanAmountAfterFees - feeAmount;
                                processedFeeTypes.add(fee.feeType); // Mark this fee type as processed
                                System.debug('Subtracted ' + fee.feeType + ' fee of ' + feeAmount + ' from loan amount. New amount: ' + loanAmountAfterFees);
                            }
                        }
                    }
                }
                rla.Loan_Amount__c = loanAmountAfterFees;
                System.debug('Set Total_Loan_Amount__c to: ' + rla.Total_Loan_Amount__c + ', Loan_Amount__c to: ' + rla.Loan_Amount__c);
                System.debug('Mapping loan status - app.loanStatus: ' + app.loanStatus + ', current rla.Status__c: ' + rla.Status__c);
                rla.Status__c = app.loanStatus;
                System.debug('After mapping - rla.Status__c: ' + rla.Status__c);
                rla.LoanSubStatus__c = app.loanStatusReason;
                // Map Date_File_Started__c - compare earliest acceptedDate from consents with applicationDate
                Date earliestConsentDate = null;
                Date applicationDate = null;
                
                // Get applicationDate if available
                if (app.applicationDate != null) {
                    try {
                        applicationDate = Date.valueOf(app.applicationDate.split('T')[0]);
                        System.debug('Application date: ' + applicationDate);
                    } catch (Exception e) {
                        System.debug('Error converting applicationDate: ' + e.getMessage() + ' - Input: ' + app.applicationDate);
                    }
                }
                
                // Find earliest acceptedDate from consents
                if (app.borrowers != null && !app.borrowers.isEmpty()) {
                    System.debug('Processing consents from ' + app.borrowers.size() + ' borrowers to find earliest acceptedDate');
                    for (Borrower borrower : app.borrowers) {
                        if (borrower.consents != null && !borrower.consents.isEmpty()) {
                            System.debug('Processing ' + borrower.consents.size() + ' consents for borrower: ' + borrower.firstName + ' ' + borrower.lastName);
                            for (Consent consent : borrower.consents) {
                                if (consent.accepted == true && consent.acceptedDate != null && consent.acceptedDate.trim() != '') {
                                    try {
                                        Date consentDate = Date.valueOf(consent.acceptedDate.split('T')[0]);
                                        System.debug('Consent acceptedDate: ' + consentDate + ' (type: ' + consent.type + ', borrower: ' + borrower.firstName + ' ' + borrower.lastName + ')');
                                        
                                        if (earliestConsentDate == null || consentDate < earliestConsentDate) {
                                            earliestConsentDate = consentDate;
                                            System.debug('New earliest consent date: ' + earliestConsentDate);
                                        }
                                    } catch (Exception e) {
                                        System.debug('Error converting consent acceptedDate: ' + e.getMessage() + ' - Input: ' + consent.acceptedDate);
                                    }
                                }
                            }
                        } else {
                            System.debug('No consents available for borrower: ' + borrower.firstName + ' ' + borrower.lastName);
                        }
                    }
                } else {
                    System.debug('No borrowers available');
                }
                
                // Compare dates and set Date_File_Started__c to the earlier date
                if (earliestConsentDate != null && applicationDate != null) {
                    if (earliestConsentDate <= applicationDate) {
                        rla.Date_File_Started__c = earliestConsentDate;
                        System.debug('Set Date_File_Started__c to earliest consent date: ' + earliestConsentDate + ' (earlier than application date: ' + applicationDate + ')');
                    } else {
                        rla.Date_File_Started__c = applicationDate;
                        System.debug('Set Date_File_Started__c to application date: ' + applicationDate + ' (earlier than earliest consent date: ' + earliestConsentDate + ')');
                    }
                } else if (earliestConsentDate != null) {
                    rla.Date_File_Started__c = earliestConsentDate;
                    System.debug('Set Date_File_Started__c to earliest consent date: ' + earliestConsentDate + ' (no application date available)');
                } else if (applicationDate != null) {
                    rla.Date_File_Started__c = applicationDate;
                    System.debug('Set Date_File_Started__c to application date: ' + applicationDate + ' (no consent dates available)');
                } else if (app.declarations != null && !app.declarations.isEmpty()) {
                    // Fallback to completedDate from declarations if no consents or applicationDate
                    for (Declaration declaration : app.declarations) {
                        if (declaration.completedDate != null && declaration.completedDate.trim() != '') {
                            try {
                                rla.Date_File_Started__c = Date.valueOf(declaration.completedDate.split('T')[0]);
                                System.debug('Set Date_File_Started__c from declarations completedDate (fallback): ' + rla.Date_File_Started__c);
                                break; // Use the first valid completedDate found
                            } catch (Exception e) {
                                System.debug('Error converting declaration completedDate: ' + e.getMessage() + ' - Input: ' + declaration.completedDate);
                            }
                        }
                    }
                }
                
                // Debug the Date_File_Started__c value safely
                try {
                    System.debug('Date_File_Started__c after mapping: ' + rla.Date_File_Started__c);
                    if (rla.Date_File_Started__c == null) {
                        System.debug('Date_File_Started__c could not be determined from consents, applicationDate, or declarations completedDate');
                    }
                } catch (Exception e) {
                    System.debug('Error accessing Date_File_Started__c field: ' + e.getMessage());
                    System.debug('This might indicate the field was not included in the SOQL query');
                }
                rla.Loan_to_Value__c = app.ltvRatio;
                rla.Combined_Loan_to_Value__c = app.cltvRatio;
                rla.Top_End_Debt_to_Income__c = app.dtiRatio;
                // Channelc__c is always set to "Brokered" regardless of API data
                rla.Channelc__c = 'Brokered';
                System.debug('Set Channelc__c to: Brokered');
                rla.API_Details__c = 'Loan updated by API ' + System.now();
                
                // Map lender name from product information with translations
                System.debug('=== LENDER NAME MAPPING START ===');
                System.debug('productData: ' + (productData != null ? 'not null' : 'null'));
                System.debug('productData.lenderName: ' + (productData != null ? productData.lenderName : 'null'));
                
                String translatedLenderName = null; // Declare at higher scope for use in broker compensation calculation
                
                if (productData != null && productData.lenderName != null) {
                    translatedLenderName = productData.lenderName;
                    System.debug('Original lender name: ' + productData.lenderName);
                    
                    // Apply lender name translations first
                    if (productData.lenderName == 'Jmac Lending') {
                        translatedLenderName = 'JMAC Lending';
                        System.debug('Translated Jmac Lending to JMAC Lending');
                    } else if (productData.lenderName == 'Pennymac' || productData.lenderName == 'PennyMac') {
                        translatedLenderName = 'PennyMac Financial';
                        System.debug('Translated ' + productData.lenderName + ' to PennyMac Financial');
                    } else if (productData.lenderName == 'Rocket Mortgage') {
                        translatedLenderName = 'Rocket Pro TPO';
                        System.debug('Translated Rocket Mortgage to Rocket Pro TPO');
                    } else if (productData.lenderName == 'Sierra Pacific') {
                        translatedLenderName = 'Sierra Pacific Mortgage';
                        System.debug('Translated Sierra Pacific to Sierra Pacific Mortgage');
                    } else {
                        System.debug('No translation needed for lenderName: ' + productData.lenderName);
                    }
                    
                    // Skip setting Lender_Name__c if either input OR output is "Undefined" to preserve existing data
                    if (productData.lenderName == 'Undefined' || translatedLenderName == 'Undefined') {
                        System.debug('Input or output lender name is "Undefined" (input: ' + productData.lenderName + ', output: ' + translatedLenderName + ') - preserving existing Lender_Name__c value: ' + rla.Lender_Name__c);
                        translatedLenderName = rla.Lender_Name__c; // Use existing value for broker compensation calculation
                    } else {
                        rla.Lender_Name__c = translatedLenderName;
                        System.debug('Set Lender_Name__c to: ' + translatedLenderName);
                    }
                } else {
                    System.debug('No lender name to map - productData: ' + (productData != null ? 'not null' : 'null') + 
                               ', lenderName: ' + (productData != null && productData.lenderName != null ? productData.lenderName : 'null'));
                }
                System.debug('=== LENDER NAME MAPPING END ===');
                
                // Map mortgage type from product information to Loan_Product__c
                System.debug('=== MORTGAGE TYPE MAPPING START ===');
                System.debug('productData: ' + (productData != null ? 'not null' : 'null'));
                System.debug('productData.mortgageType: ' + (productData != null ? productData.mortgageType : 'null'));
                
                if (productData != null && productData.mortgageType != null) {
                    String mappedLoanProduct = productData.mortgageType;
                    System.debug('Original mortgage type: ' + productData.mortgageType);
                    
                    // Apply mortgage type translations with case-insensitive pattern matching
                    String mortgageTypeLower = productData.mortgageType.toLowerCase();
                    
                    if (productData.mortgageType == 'Conforming') {
                        mappedLoanProduct = 'Conventional';
                        System.debug('Translated Conforming to Conventional');
                    } else if (mortgageTypeLower.contains('fha')) {
                        mappedLoanProduct = 'FHA';
                        System.debug('Translated ' + productData.mortgageType + ' to FHA (contains FHA variation)');
                    } else if (mortgageTypeLower.contains('va')) {
                        mappedLoanProduct = 'VA';
                        System.debug('Translated ' + productData.mortgageType + ' to VA (contains VA variation)');
                    } else if (mortgageTypeLower.contains('usda')) {
                        mappedLoanProduct = 'USDA';
                        System.debug('Translated ' + productData.mortgageType + ' to USDA (contains USDA variation)');
                    } else if (mortgageTypeLower.contains('nonqm') || mortgageTypeLower.contains('nonconforming') || mortgageTypeLower.contains('non-conforming') || mortgageTypeLower.contains('non-qm')) {
                        mappedLoanProduct = 'Non-QM';
                        System.debug('Translated ' + productData.mortgageType + ' to Non-QM (contains Non-QM variation)');
                    } else if (productData.mortgageType == 'Second Mortgage') {
                        mappedLoanProduct = 'Other';
                        System.debug('Translated Second Mortgage to Other');
                    } else if (productData.mortgageType == 'HELOAN') {
                        mappedLoanProduct = 'HELOC';
                        System.debug('Translated HELOAN to HELOC');
                    } else if (productData.mortgageType == 'Unknown') {
                        mappedLoanProduct = '';
                        System.debug('Translated Unknown to blank value');
                    } else {
                        System.debug('No translation needed for mortgageType: ' + productData.mortgageType);
                    }
                    
                    rla.Loan_Product__c = mappedLoanProduct;
                    System.debug('Set Loan_Product__c to: ' + mappedLoanProduct);
                    System.debug('Loan_Product__c field value after assignment: ' + rla.Loan_Product__c);
                } else {
                    // Fallback to app.loanType if product data is not available
                    rla.Loan_Product__c = app.loanType;
                    System.debug('No mortgage type from product data, using app.loanType as fallback: ' + app.loanType);
                }
                System.debug('=== MORTGAGE TYPE MAPPING END ===');
                
                // Map product name from product information to Additional_Loan_Product_Details__c
                System.debug('=== PRODUCT NAME MAPPING START ===');
                System.debug('productData: ' + (productData != null ? 'not null' : 'null'));
                System.debug('productData.productName: ' + (productData != null ? productData.productName : 'null'));
                
                if (productData != null && productData.productName != null) {
                    rla.Additional_Loan_Product_Details__c = truncateToLength(productData.productName, 255);
                    System.debug('Set Additional_Loan_Product_Details__c to: ' + rla.Additional_Loan_Product_Details__c);
                } else {
                    System.debug('No product name available from product data');
                }
                System.debug('=== PRODUCT NAME MAPPING END ===');
                
                // Map wire information
                System.debug('=== WIRE INFORMATION MAPPING START ===');
                
                // Map wireAmountReceived to Wire_Check_Amount_Received__c
                if (app.wireAmountReceived != null) {
                    rla.Wire_Check_Amount_Received__c = app.wireAmountReceived;
                    System.debug('Set Wire_Check_Amount_Received__c to: ' + app.wireAmountReceived);
                } else {
                    rla.Wire_Check_Amount_Received__c = null;
                    System.debug('Set Wire_Check_Amount_Received__c to null (wireAmountReceived not provided)');
                }
                
                // Map wireReceivedDate to Wire_Check_Date_Received__c
                if (app.wireReceivedDate != null && app.wireReceivedDate.trim() != '') {
                    try {
                        // Parse the ISO date string and convert to Date
                        Date wireDate = Date.valueOf(app.wireReceivedDate.split('T')[0]);
                        rla.Wire_Check_Date_Received__c = wireDate;
                        System.debug('Set Wire_Check_Date_Received__c to: ' + wireDate + ' (from: ' + app.wireReceivedDate + ')');
                    } catch (Exception e) {
                        System.debug('Error parsing wireReceivedDate: ' + app.wireReceivedDate + ', error: ' + e.getMessage());
                        rla.Wire_Check_Date_Received__c = null;
                    }
                } else {
                    rla.Wire_Check_Date_Received__c = null;
                    System.debug('Set Wire_Check_Date_Received__c to null (wireReceivedDate not provided)');
                }
                
                System.debug('=== WIRE INFORMATION MAPPING END ===');
                
                // Map compensation from product information to Compensation_Type__c
                System.debug('=== COMPENSATION MAPPING START ===');
                System.debug('productData: ' + (productData != null ? 'not null' : 'null'));
                System.debug('productData.compensation: ' + (productData != null ? productData.compensation : 'null'));
                
                if (productData != null && productData.compensation != null) {
                    System.debug('Original compensation: ' + productData.compensation);
                    
                    // Convert compensation to string for processing
                    String compensationStr = String.valueOf(productData.compensation);
                    System.debug('Compensation as string: ' + compensationStr);
                    
                    // Check if compensation is 0 or null (after conversion)
                    if (compensationStr == '0' || compensationStr == 'null' || compensationStr.trim() == '') {
                        System.debug('Compensation is 0, null, or empty, keeping default "Lender Paid"');
                    } else {
                        // Apply compensation mapping logic for string values
                        if (compensationStr.containsIgnoreCase('Lender')) {
                            rla.Compensation_Type__c = 'Lender Paid';
                            System.debug('Compensation contains "Lender", setting to "Lender Paid"');
                        } else if (compensationStr.containsIgnoreCase('Borrower')) {
                            rla.Compensation_Type__c = 'Borrower Paid';
                            System.debug('Compensation contains "Borrower", setting to "Borrower Paid"');
                        } else {
                            System.debug('Compensation does not contain "Lender" or "Borrower", keeping default "Lender Paid"');
                        }
                    }
                } else {
                    System.debug('No compensation data available, keeping default "Lender Paid"');
                }
                
                System.debug('Final Compensation_Type__c: ' + rla.Compensation_Type__c);
                System.debug('=== COMPENSATION MAPPING END ===');
                
                // Calculate broker compensation percentage from FIRST BrokerFee only (to avoid double-counting)
                System.debug('=== BROKER COMPENSATION PERCENTAGE CALCULATION START ===');
                
                if (app.fees != null && !app.fees.isEmpty()) {
                    // **USE FIRST BROKER FEE ONLY** - Find first BrokerFee to avoid double-counting in calculations
                    Decimal firstBrokerFeeAmount = null;
                    String firstBrokerFeeId = null;
                    Integer brokerFeeCount = 0;
                    
                    // First pass: Find the first BrokerFee and sum all 5 amount fields
                    for (Fee fee : app.fees) {
                        if (fee.feeType == 'BrokerFee') {
                            // Sum all 5 amount fields: borrowerPacAmount, borrowerPocAmount, sellerPacAmount, sellerPocAmount, and othersPaidAmount
                            Decimal brokerFeeAmount = 0;
                            
                            if (fee.borrowerPacAmount != null) {
                                brokerFeeAmount += fee.borrowerPacAmount;
                            }
                            if (fee.borrowerPocAmount != null) {
                                brokerFeeAmount += fee.borrowerPocAmount;
                            }
                            if (fee.sellerPacAmount != null) {
                                brokerFeeAmount += fee.sellerPacAmount;
                            }
                            if (fee.sellerPocAmount != null) {
                                brokerFeeAmount += fee.sellerPocAmount;
                            }
                            if (fee.othersPaidAmount != null) {
                                brokerFeeAmount += fee.othersPaidAmount;
                            }
                            
                            if (brokerFeeAmount > 0) {
                                brokerFeeCount++;
                                System.debug('Found BrokerFee #' + brokerFeeCount + ': loanFeeId=' + fee.loanFeeId + 
                                    ', borrowerPacAmount=' + (fee.borrowerPacAmount != null ? fee.borrowerPacAmount : 0) +
                                    ', borrowerPocAmount=' + (fee.borrowerPocAmount != null ? fee.borrowerPocAmount : 0) +
                                    ', sellerPacAmount=' + (fee.sellerPacAmount != null ? fee.sellerPacAmount : 0) +
                                    ', sellerPocAmount=' + (fee.sellerPocAmount != null ? fee.sellerPocAmount : 0) +
                                    ', othersPaidAmount=' + (fee.othersPaidAmount != null ? fee.othersPaidAmount : 0) +
                                    ', total=' + brokerFeeAmount);
                                
                                // Only use the first BrokerFee for calculations
                                if (firstBrokerFeeAmount == null) {
                                    firstBrokerFeeAmount = brokerFeeAmount;
                                    firstBrokerFeeId = fee.loanFeeId;
                                    System.debug('Using FIRST BrokerFee for calculations: loanFeeId=' + firstBrokerFeeId + ', total amount=' + firstBrokerFeeAmount);
                                }
                            } else {
                                System.debug('Found BrokerFee with loanFeeId=' + fee.loanFeeId + ' but sum of all amount fields is 0');
                            }
                        }
                    }
                    
                    System.debug('=== BROKER FEE SUMMARY ===');
                    System.debug('Total BrokerFee records found: ' + brokerFeeCount);
                    System.debug('First BrokerFee total amount (sum of all 5 fields, for calculations): ' + firstBrokerFeeAmount);
                    if (brokerFeeCount > 1) {
                        System.debug('WARNING: Multiple BrokerFee records found, but only using the FIRST one for Broker_Compensation_Percentage__c and Listed_Revenue__c calculations to avoid double-counting');
                    }
                    System.debug('=== END BROKER FEE SUMMARY ===');
                    
                    // Calculate percentages using ONLY the FIRST BrokerFee total amount (sum of all 5 fields)
                    if (firstBrokerFeeAmount != null && firstBrokerFeeAmount > 0 && app.loanAmount != null && app.loanAmount > 0) {
                        Decimal brokerCompensationPercentage;
                        
                        Decimal flatFeeDeduction = null;
                        if (rla.Compensation_Type__c == 'Lender Paid') {
                            Date tridAppDateForFlatFee = resolveTridApplicationDateForFlatFee(app, rla);
                            flatFeeDeduction = getLenderPaidFlatFeeDeduction(translatedLenderName, app.transactionType, tridAppDateForFlatFee);
                        }
                        
                        if (flatFeeDeduction != null) {
                            Decimal adjustedBrokerFee = firstBrokerFeeAmount - flatFeeDeduction;
                            if (adjustedBrokerFee < 0) {
                                adjustedBrokerFee = 0;
                            }
                            brokerCompensationPercentage = (adjustedBrokerFee / app.loanAmount) * 100;
                            System.debug('Flat fee adjusted broker compensation %: (' + firstBrokerFeeAmount + ' - ' + flatFeeDeduction + ') / ' + app.loanAmount + ' * 100 = ' + brokerCompensationPercentage + '% (lender: ' + translatedLenderName + ')');
                        } else {
                            brokerCompensationPercentage = (firstBrokerFeeAmount / app.loanAmount) * 100;
                            System.debug('Standard calculation: ' + firstBrokerFeeAmount + ' / ' + app.loanAmount + ' * 100 = ' + brokerCompensationPercentage + '%');
                        }
                        
                        rla.Broker_Compensation_Percentage__c = brokerCompensationPercentage;
                        System.debug('Set Broker_Compensation_Percentage__c to: ' + brokerCompensationPercentage + '%');
                        
                        // Map FIRST BrokerFee total amount to Listed_Revenue__c - always use full amount regardless of lender
                        // Listed Revenue should equal the exact Broker Fee amount from Sonar (sum of all 5 fields)
                        rla.Listed_Revenue__c = firstBrokerFeeAmount;
                        System.debug('Set Listed_Revenue__c from FIRST BrokerFee total amount (sum of all 5 fields): ' + firstBrokerFeeAmount);
                    } else if (brokerFeeCount > 0) {
                        System.debug('BrokerFee(s) found but total amount is 0 or loan amount is invalid');
                    } else {
                        System.debug('No BrokerFee records found in the payload');
                    }
                }
                
                System.debug('Final Broker_Compensation_Percentage__c: ' + rla.Broker_Compensation_Percentage__c + '%');
                System.debug('=== BROKER COMPENSATION PERCENTAGE CALCULATION END ===');
                
                // Map rate from product information to InterestRate__c
                System.debug('=== RATE MAPPING START ===');
                System.debug('productData: ' + (productData != null ? 'not null' : 'null'));
                System.debug('productData.rate: ' + (productData != null && productData.rate != null ? String.valueOf(productData.rate) : 'null'));
                
                if (productData != null && productData.rate != null) {
                    // Convert rate from decimal (0.0687500000) to percentage (6.875)
                    Decimal ratePercentage = productData.rate * 100;
                    rla.InterestRate__c = ratePercentage;
                    System.debug('Original rate: ' + productData.rate + ', converted to percentage: ' + ratePercentage);
                    System.debug('Set InterestRate__c to: ' + ratePercentage);
                } else {
                    System.debug('No rate data available from product data');
                }
                System.debug('=== RATE MAPPING END ===');
                
                // Map loan term from product information to Loan_Term_Months__c
                System.debug('=== LOAN TERM MAPPING START ===');
                System.debug('productData: ' + (productData != null ? 'not null' : 'null'));
                System.debug('productData.loanTerm: ' + (productData != null && productData.loanTerm != null ? String.valueOf(productData.loanTerm) : 'null'));
                
                if (productData != null && productData.loanTerm != null) {
                    rla.Loan_Term_Months__c = productData.loanTerm;
                    System.debug('Set Loan_Term_Months__c to: ' + productData.loanTerm);
                } else {
                    System.debug('No loan term data available from product data');
                }
                System.debug('=== LOAN TERM MAPPING END ===');
                
                // Map mortgage product payment information
                System.debug('=== MORTGAGE PRODUCT PAYMENT MAPPING START ===');
                System.debug('productData: ' + (productData != null ? 'not null' : 'null'));
                
                if (productData != null) {
                    // Map principal and interest payment
                    if (productData.principalAndInterest != null) {
                        rla.Proposed_Payment_First_Mortgage__c = productData.principalAndInterest;
                        System.debug('Set Proposed_Payment_First_Mortgage__c to: ' + productData.principalAndInterest);
                    } else {
                        System.debug('No principal and interest payment available from product data');
                    }
                    
                    // Map monthly mortgage insurance
                    if (productData.monthlyMortgageInsurance != null) {
                        rla.Proposed_Payment_Mortgage_Insurance__c = String.valueOf(productData.monthlyMortgageInsurance);
                        System.debug('Set Proposed_Payment_Mortgage_Insurance__c to: ' + productData.monthlyMortgageInsurance);
                    } else {
                        System.debug('No monthly mortgage insurance available from product data');
                    }
                } else {
                    System.debug('No product data available for payment mapping');
                }
                
                System.debug('=== MORTGAGE PRODUCT PAYMENT MAPPING END ===');
                
                // Map transactionType to LoanPurpose__c
                if (app.transactionType != null) {
                    System.debug('Mapping transactionType: ' + app.transactionType + ' to LoanPurpose__c');
                    if (app.transactionType == 'Purchase' || app.transactionType == 'PreApproval') {
                        rla.LoanPurpose__c = 'Purchase';
                    } else if (app.transactionType == 'RefinanceRateandTerm') {
                        rla.LoanPurpose__c = 'Rate/Term Refinance';
                    } else if (app.transactionType == 'RefinanceCashOut') {
                        rla.LoanPurpose__c = 'Cash-Out Refinance';
                    } else {
                        rla.LoanPurpose__c = 'Other';
                    }
                    System.debug('Set LoanPurpose__c to: ' + rla.LoanPurpose__c);
                }
                
                // Ensure First_Time_Homebuyer__c is set to a proper Boolean value
                rla.First_Time_Homebuyer__c = app.firstTimeHomeBuyer != null ? app.firstTimeHomeBuyer : false;
                System.debug('Setting First_Time_Homebuyer__c to: ' + rla.First_Time_Homebuyer__c);
                
                // Check fees for broker compensation string - handle multiple matches
                if (app.fees != null && !app.fees.isEmpty()) {
                    List<String> brokerCompStrings = new List<String>();
                    for (Fee fee : app.fees) {
                        if (fee.loanFeeId == '1917' || fee.loanFeeId == '1889') {
                            if (fee.feeBuilderArgs != null && fee.feeBuilderArgs.trim() != '') {
                                brokerCompStrings.add(fee.feeBuilderArgs);
                                System.debug('Found broker compensation string from fee ' + fee.loanFeeId + ': ' + fee.feeBuilderArgs);
                            }
                        }
                    }
                    
                    // Combine multiple broker compensation strings if found
                    if (!brokerCompStrings.isEmpty()) {
                        rla.Broker_Compensation_String__c = truncateToLength(String.join(brokerCompStrings, '; '), 32768);
                        System.debug('Set Broker_Compensation_String__c to: ' + rla.Broker_Compensation_String__c);
                    }
                }
                
                // Map borrower information if available
                Borrower primaryBorrower;
                Borrower coBorrower;
                if (app.borrowerPairs != null && !app.borrowerPairs.isEmpty() && app.borrowers != null && !app.borrowers.isEmpty()) {
                    // First Priority: Look for a borrower pair with order = 1 that has both primary and secondary refs
                    BorrowerPair orderOnePair = null;
                    for (BorrowerPair pair : app.borrowerPairs) {
                        if (pair.order != null && pair.order == 1) {
                            orderOnePair = pair;
                            break;
                        }
                    }
                    
                    if (orderOnePair != null && orderOnePair.primaryBorrowerRef != null && orderOnePair.secondaryBorrowerRef != null) {
                        // Use the order=1 pair with both primary and secondary borrowers
                        String primaryId = orderOnePair.primaryBorrowerRef;
                        String secondaryId = orderOnePair.secondaryBorrowerRef;
                        
                        for (Borrower b : app.borrowers) {
                            if (b.borrowerId == primaryId) {
                                primaryBorrower = b;
                            } else if (b.borrowerId == secondaryId) {
                                coBorrower = b;
                            }
                        }
                    } else {
                        // Second Priority: Look for separate pairs with order = 1 and order = 2
                        BorrowerPair primaryPair = null;
                        BorrowerPair coBorrowerPair = null;
                        
                        for (BorrowerPair pair : app.borrowerPairs) {
                            if (pair.order != null && pair.order == 1) {
                                primaryPair = pair;
                            } else if (pair.order != null && pair.order == 2) {
                                coBorrowerPair = pair;
                            }
                        }
                        
                        // Map primary borrower (order = 1)
                        if (primaryPair != null) {
                            String primaryId = primaryPair.primaryBorrowerRef;
                            for (Borrower b : app.borrowers) {
                                if (b.borrowerId == primaryId) {
                                    primaryBorrower = b;
                                    break;
                                }
                            }
                        }
                        
                        // Map co-borrower (order = 2)
                        if (coBorrowerPair != null) {
                            String coBorrowerId = coBorrowerPair.primaryBorrowerRef;
                            for (Borrower b : app.borrowers) {
                                if (b.borrowerId == coBorrowerId) {
                                    coBorrower = b;
                                    break;
                                }
                            }
                        }
                        
                        // Fallback: if no order-based pairs found, use old logic
                        if (primaryBorrower == null && coBorrower == null) {
                            BorrowerPair pair = app.borrowerPairs[0];
                            String primaryId = pair.primaryBorrowerRef;
                            String secondaryId = pair.secondaryBorrowerRef;
                            for (Borrower b : app.borrowers) {
                                if (b.borrowerId == primaryId) {
                                    primaryBorrower = b;
                                } else if (secondaryId != null && b.borrowerId == secondaryId) {
                                    coBorrower = b;
                                }
                            }
                        }
                    }
                } else if (app.borrowers != null && !app.borrowers.isEmpty()) {
                    // Fallback to old logic if no borrowerPairs
                    primaryBorrower = app.borrowers[0];
                    if (app.borrowers.size() > 1) {
                        coBorrower = app.borrowers[1];
                    }
                }
                if (primaryBorrower != null) {
                    // Check if primary borrower is Jane Doe - if so, set all fields to null
                    if (isJaneDoe(primaryBorrower)) {
                        System.debug('Primary borrower is Jane Doe - setting all fields to null');
                        rla.BorrowerFirstNamec__c = null;
                        rla.BorrowerLastNamec__c = null;
                        rla.BorrowerEmailc__c = null;
                        rla.BorrowerHomePhonec__c = null;
                        rla.BorrowerSSNc__c = null;
                        rla.Borrowerdobc__c = null;
                        rla.BorrowerMaritalStatusc__c = null;
                    } else {
                        rla.BorrowerFirstNamec__c = primaryBorrower.firstName;
                        rla.BorrowerLastNamec__c = primaryBorrower.lastName;
                        rla.BorrowerEmailc__c = primaryBorrower.email;
                        rla.BorrowerHomePhonec__c = primaryBorrower.phone;
                        rla.BorrowerSSNc__c = primaryBorrower.taxIdentifier;
                        rla.Borrowerdobc__c = primaryBorrower.dateOfBirth != null ? Date.valueOf(primaryBorrower.dateOfBirth.split('T')[0]) : null;
                        rla.BorrowerMaritalStatusc__c = primaryBorrower.maritalStatus;
                    }
                }
                if (coBorrower != null) {
                    // Check if co-borrower is Jane Doe - if so, set all fields to null
                    if (isJaneDoe(coBorrower)) {
                        System.debug('Co-borrower is Jane Doe - setting all fields to null');
                        rla.CoBorrowerFirstNamec__c = null;
                        rla.CoBorrowerLastNamec__c = null;
                        rla.CoBorrowerEmailc__c = null;
                        rla.CoBorrowerCellc__c = null;
                        rla.CoBorrowerdobc__c = null;
                    } else {
                        rla.CoBorrowerFirstNamec__c = coBorrower.firstName;
                        rla.CoBorrowerLastNamec__c = coBorrower.lastName;
                        rla.CoBorrowerEmailc__c = coBorrower.email;
                        rla.CoBorrowerCellc__c = coBorrower.phone;
                        rla.CoBorrowerdobc__c = coBorrower.dateOfBirth != null ? Date.valueOf(coBorrower.dateOfBirth.split('T')[0]) : null;
                        System.debug('Mapped co-borrower information - Name: ' + coBorrower.firstName + ' ' + coBorrower.lastName + ', Email: ' + coBorrower.email + ', Phone: ' + coBorrower.phone);
                    }
                } else {
                    // No co-borrower - set all co-borrower fields to NULL (except Boolean fields which should be FALSE)
                    System.debug('No co-borrower found - setting all co-borrower fields to null (Boolean fields to false)');
                    rla.Co_Borrower__c = null;
                    rla.HasCoborrowerc__c = false; // Boolean field - set to FALSE when no co-borrower
                    rla.CoBorrowerAuthorizedCreditReportc__c = false; // Boolean field - set to FALSE when no co-borrower
                    rla.CoBorrowerCellc__c = null;
                    rla.CoBorrowerDaysConsentc__c = null;
                    rla.CoBorrowerdobc__c = null;
                    rla.CoBorrowerEmailc__c = null;
                    rla.CoBorrowerEquifaxc__c = null;
                    rla.CoBorrowerExperianFICOc__c = null;
                    rla.CoBorrowerFirstNamec__c = null;
                    rla.CoBorrowerHomePhonec__c = null;
                    rla.CoBorrowerLastNamec__c = null;
                    rla.CoBorrowerMaritalStatusc__c = null;
                    rla.CoBorrowerMinimumFICOc__c = null;
                    rla.CoBorrowerMortgageServicec__c = false; // Boolean field - set to FALSE when no co-borrower
                    rla.CoBorrowerSSNc__c = null;
                    rla.CoBorrowerTransactionPurposec__c = null;
                    rla.CoBorrowerTransUnionc__c = null;
                    rla.Co_Borrower_Branch_of_Service__c = null;
                }
                
                // Map subject property information if available
                if (app.subjectProperty != null) {
                    // Handle Date_Address_Found__c field BEFORE updating Property_Address__c
                    String sonarAddress = app.subjectProperty.street1;
                    String currentAddress = rla.Property_Address__c; // Current Salesforce address before update
                    
                    // Check if Sonar has a valid address (not null, TBD, tbd, N/A, or n/a)
                    Boolean sonarHasValidAddress = sonarAddress != null && 
                        !sonarAddress.equalsIgnoreCase('TBD') && 
                        !sonarAddress.equalsIgnoreCase('N/A');
                    
                    // Check if current Salesforce address is null or placeholder
                    Boolean currentAddressIsPlaceholder = currentAddress == null || 
                        currentAddress.equalsIgnoreCase('TBD') || 
                        currentAddress.equalsIgnoreCase('N/A');
                    
                    if (sonarHasValidAddress && currentAddressIsPlaceholder) {
                        // Sonar has valid address and Salesforce has placeholder - set today's date
                        rla.Date_Address_Found__c = Date.today();
                        System.debug('Set Date_Address_Found__c to today: ' + Date.today() + ' (Sonar address: ' + sonarAddress + ', Current address: ' + currentAddress + ')');
                    } else if (!sonarHasValidAddress) {
                        // Sonar has null or placeholder address - set field to null
                        rla.Date_Address_Found__c = null;
                        System.debug('Set Date_Address_Found__c to null (Sonar address is null or placeholder: ' + sonarAddress + ')');
                    } else {
                        System.debug('Date_Address_Found__c unchanged (Sonar address: ' + sonarAddress + ', Current address: ' + currentAddress + ')');
                    }
                    
                    // Now update the property address fields
                    rla.Property_Address__c = app.subjectProperty.street1 != null ? app.subjectProperty.street1 : 'TBD';
                    rla.Property_City__c = app.subjectProperty.city;
                    rla.Property_State__c = app.subjectProperty.state;
                    rla.Property_Zip_Code__c = app.subjectProperty.zipCode;
                    
                    // Map property type with translations
                    if (app.subjectProperty.propertyType != null) {
                        String mappedPropertyType = app.subjectProperty.propertyType;
                        System.debug('Original property type: ' + app.subjectProperty.propertyType);
                        
                        if (app.subjectProperty.propertyType == 'MultiFamily') {
                            mappedPropertyType = '2-4 Units';
                            System.debug('Translated MultiFamily to 2-4 Units');
                        } else if (app.subjectProperty.propertyType == 'SingleFamily') {
                            mappedPropertyType = '1 Unit';
                            System.debug('Translated SingleFamily to 1 Unit');
                        } else {
                            System.debug('No translation needed for propertyType: ' + app.subjectProperty.propertyType);
                        }
                        
                        rla.Property_Type__c = mappedPropertyType;
                        System.debug('Set Property_Type__c to: ' + mappedPropertyType);
                    } else {
                        System.debug('No property type available');
                    }
                    
                    // Map number of units
                    if (app.subjectProperty.numberOfUnits != null) {
                        rla.Subject_Property_Number_of_Units__c = app.subjectProperty.numberOfUnits;
                        System.debug('Set Subject_Property_Number_of_Units__c to: ' + app.subjectProperty.numberOfUnits);
                    } else {
                        System.debug('No numberOfUnits available from subject property');
                    }
                    
                    // Map occupancy type with translations
                    if (app.subjectProperty.occupancyType != null) {
                        String mappedOccupancyType = app.subjectProperty.occupancyType;
                        System.debug('Original occupancy type: ' + app.subjectProperty.occupancyType);
                        
                        if (app.subjectProperty.occupancyType == 'PrimaryResidence') {
                            mappedOccupancyType = 'Primary';
                            System.debug('Translated PrimaryResidence to Primary');
                        } else if (app.subjectProperty.occupancyType == 'SecondHome') {
                            mappedOccupancyType = 'Second Home';
                            System.debug('Translated SecondHome to Second Home');
                        } else if (app.subjectProperty.occupancyType == 'InvestmentProperty') {
                            mappedOccupancyType = 'Investment';
                            System.debug('Translated InvestmentProperty to Investment');
                        } else {
                            System.debug('No translation needed for occupancyType: ' + app.subjectProperty.occupancyType);
                        }
                        
                        rla.Occupancy_Type__c = mappedOccupancyType;
                        System.debug('Set Occupancy_Type__c to: ' + mappedOccupancyType);
                    } else {
                        System.debug('No occupancy type available, keeping default "Primary"');
                    }
                    
                    // Map additional subject property payment information
                    System.debug('=== SUBJECT PROPERTY PAYMENT MAPPING START ===');
                    
                    // Map purchase price
                    if (app.subjectProperty.purchasePrice != null) {
                        rla.Subject_Property_Purchase_Price__c = app.subjectProperty.purchasePrice;
                        System.debug('Set Subject_Property_Purchase_Price__c to: ' + app.subjectProperty.purchasePrice);
                    } else {
                        System.debug('No purchase price available from subject property');
                    }
                    
                    // Map HOA fees
                    if (app.subjectProperty.hoaFees != null) {
                        rla.Proposed_Payment_HOA_Dues__c = String.valueOf(app.subjectProperty.hoaFees);
                        System.debug('Set Proposed_Payment_HOA_Dues__c to: ' + app.subjectProperty.hoaFees);
                    } else {
                        System.debug('No HOA fees available from subject property');
                    }
                    
                    // Map insurance (divide by 12 for monthly amount)
                    if (app.subjectProperty.insurance != null) {
                        Decimal monthlyInsurance = app.subjectProperty.insurance / 12;
                        rla.Proposed_Payment_Homeowners_Insurance__c = String.valueOf(monthlyInsurance);
                        System.debug('Set Proposed_Payment_Homeowners_Insurance__c to: ' + monthlyInsurance + ' (annual insurance: ' + app.subjectProperty.insurance + ' / 12)');
                    } else {
                        System.debug('No insurance available from subject property');
                    }
                    
                    // Map other expenses
                    if (app.subjectProperty.otherExpenses != null) {
                        rla.Proposed_Payment_Other_Expenses__c = app.subjectProperty.otherExpenses;
                        System.debug('Set Proposed_Payment_Other_Expenses__c to: ' + app.subjectProperty.otherExpenses);
                    } else {
                        System.debug('No other expenses available from subject property');
                    }
                    
                    // Map taxes (divide by 12 for monthly amount)
                    if (app.subjectProperty.taxes != null) {
                        Decimal monthlyTaxes = app.subjectProperty.taxes / 12;
                        rla.Proposed_Payment_Property_Taxes__c = String.valueOf(monthlyTaxes);
                        System.debug('Set Proposed_Payment_Property_Taxes__c to: ' + monthlyTaxes + ' (annual taxes: ' + app.subjectProperty.taxes + ' / 12)');
                    } else {
                        System.debug('No taxes available from subject property');
                    }
                    
                    System.debug('=== SUBJECT PROPERTY PAYMENT MAPPING END ===');
                } else {
                    // No subject property data - set Date_Address_Found__c to null
                    rla.Date_Address_Found__c = null;
                    System.debug('Set Date_Address_Found__c to null (no subject property data)');
                }
                
                // If estimatedValue is null or 0, calculate purchase price from loan amount and down payment
                if (app.subjectProperty == null || app.subjectProperty.estimatedValue == null || app.subjectProperty.estimatedValue == 0) {
                    Decimal loanAmount = app.loanAmount != null ? app.loanAmount : 0;
                    Decimal downPayment = app.downPayment != null ? app.downPayment : 0;
                    rla.Property_Value__c = loanAmount + downPayment;
                } else {
                    rla.Property_Value__c = app.subjectProperty.estimatedValue;
                }
                
                // Map asset information if available
                if (app.assets != null && !app.assets.isEmpty()) {
                    Decimal totalAssets = 0;
                    for (Asset asset : app.assets) {
                        if (asset.amount != null) {
                            totalAssets += asset.amount;
                        }
                    }
                }
                
                // Map income information if available
                if (app.income != null && !app.income.isEmpty()) {
                    Decimal totalIncome = 0;
                    for (Income inc : app.income) {
                        if (inc.amount != null) {
                            totalIncome += inc.amount;
                        }
                    }
                }
                
                // Map declaration information if available
                if (app.declarations != null && !app.declarations.isEmpty()) {
                    Declaration declaration = app.declarations[0];
                }
                
                // Set co-borrower flag
                System.debug('Borrowers array size: ' + (app.borrowers != null ? String.valueOf(app.borrowers.size()) : 'null'));
                rla.HasCoborrowerc__c = app.borrowers != null && app.borrowers.size() > 1;
                System.debug('HasCoborrowerc__c set to: ' + rla.HasCoborrowerc__c);
                
                // Process credit scores from all borrowers
                // Find the lowest non-zero creditScore value, or NULL if none found or all are 0
                if (app.borrowers != null && !app.borrowers.isEmpty()) {
                    List<Integer> nonZeroCreditScores = new List<Integer>();
                    
                    for (Borrower borrower : app.borrowers) {
                        if (borrower.creditScore != null && borrower.creditScore > 0) {
                            nonZeroCreditScores.add(borrower.creditScore);
                            System.debug('Found creditScore ' + borrower.creditScore + ' for borrower: ' + borrower.firstName + ' ' + borrower.lastName);
                        }
                    }
                    
                    if (nonZeroCreditScores.isEmpty()) {
                        // No credit scores found or all are 0
                        rla.Credit_Score__c = null;
                        System.debug('No non-zero credit scores found - setting Credit_Score__c to null');
                    } else {
                        // Find the lowest non-zero credit score
                        nonZeroCreditScores.sort();
                        Integer lowestCreditScore = nonZeroCreditScores[0];
                        rla.Credit_Score__c = lowestCreditScore;
                        System.debug('Setting Credit_Score__c to lowest non-zero value: ' + lowestCreditScore);
                    }
                } else {
                    rla.Credit_Score__c = null;
                    System.debug('No borrowers found - setting Credit_Score__c to null');
                }
                
                // Process credit authorization consent for primary borrower
                // Find consent where borrowerRef equals primary borrowerId, type equals "CreditAuthorization"
                if (primaryBorrower != null && primaryBorrower.borrowerId != null) {
                    Boolean creditAuthorized = false;
                    Boolean consentFound = false;
                    
                    // Check all borrowers' consents to find the matching one
                    if (app.borrowers != null && !app.borrowers.isEmpty()) {
                        for (Borrower borrower : app.borrowers) {
                            if (borrower.consents != null && !borrower.consents.isEmpty()) {
                                for (Consent consent : borrower.consents) {
                                    if (consent.borrowerRef != null && 
                                        consent.borrowerRef.equals(primaryBorrower.borrowerId) &&
                                        consent.type != null &&
                                        consent.type.equals('CreditAuthorization')) {
                                        // Found matching consent
                                        consentFound = true;
                                        creditAuthorized = (consent.accepted == true);
                                        System.debug('Found CreditAuthorization consent for primary borrower ' + primaryBorrower.borrowerId + 
                                                    ' - accepted: ' + consent.accepted + ', setting BorrowerAuthorizedCreditReportc__c to ' + creditAuthorized);
                                        break;
                                    }
                                }
                                if (consentFound) {
                                    break;
                                }
                            }
                        }
                    }
                    
                    // If no matching consent found, set to false
                    if (!consentFound) {
                        System.debug('No CreditAuthorization consent found for primary borrower ' + primaryBorrower.borrowerId + 
                                    ' - setting BorrowerAuthorizedCreditReportc__c to false');
                    }
                    rla.BorrowerAuthorizedCreditReportc__c = creditAuthorized;
                } else {
                    // No primary borrower found
                    rla.BorrowerAuthorizedCreditReportc__c = false;
                    System.debug('No primary borrower found - setting BorrowerAuthorizedCreditReportc__c to false');
                }
                
                // Get application date for validation (reuse the applicationDate variable from above)
                System.debug('Application date for pre-approval validation: ' + applicationDate);
                
                // STEP 1: Check transactionType - only process pre-approval for Purchase or PreApproval transactions
                Boolean processPreApprovalDate = (app.transactionType == 'Purchase' || app.transactionType == 'PreApproval');
                if (!processPreApprovalDate) {
                    rla.Pre_Approval_Date__c = null;
                    System.debug('TransactionType is not Purchase or PreApproval (' + app.transactionType + '), setting Pre_Approval_Date__c to null');
                }
                
                // STEP 1.5: Check if a pre-approval document exists - only process pre-approval date if document exists
                Boolean hasPreApprovalDoc = false;
                if (processPreApprovalDate) {
                    hasPreApprovalDoc = hasPreApprovalDocument(app.documents);
                    System.debug('Pre-approval document exists: ' + hasPreApprovalDoc);
                    
                    if (!hasPreApprovalDoc) {
                        rla.Pre_Approval_Date__c = null;
                        processPreApprovalDate = false; // Disable pre-approval date processing
                        System.debug('No pre-approval document found, setting Pre_Approval_Date__c to null and disabling pre-approval date processing');
                    }
                }
                
                // Map key dates information if available
                if (app.keyDates != null && !app.keyDates.isEmpty()) {
                    // Variables to track dates for fallback logic
                    Date fundedDate = null;
                    Date estimatedFundingDate = null;
                    Date estimatedClosingDate = null;
                    Boolean lockExpirationDateFound = false;
                    
                    // Variables to track if each key date type was found
                    Boolean tridDateFound = false;
                    Boolean underwrittenPreApprovalFound = false;
                    Boolean underwritingFound = false;
                    Boolean approvedDateFound = false;
                    Boolean clearToCloseDateFound = false;
                    Boolean estimatedClosingDateFound = false;
                    Boolean closingDateFound = false;
                    Boolean fundedDateFound = false;
                    Boolean estimatedFundingDateFound = false;
                    Boolean closingDisclosureSignedFound = false;
                    Boolean rateLockDateFound = false;
                    Boolean rateLockExpirationDateFound = false;
                    Boolean appraisalContingencyDateFound = false;
                    Boolean disclosuresSentFound = false;
                    Boolean finalActionDateFound = false;
                    Boolean applicationDateFound = false;
                    Boolean processingFound = false;
                    Boolean appraisalOrderedDateFound = false;
                    Boolean titleReportOrderDateFound = false;
                    Boolean closingDisclosureSentFound = false;
                    Boolean contractDateFound = false;
                    
                    for (KeyDate keyDate : app.keyDates) {
                        if (keyDate.value != null && keyDate.value != '') {
                            Date convertedDate = convertISOStringToDate(keyDate.value);
                            
                            if (keyDate.name == 'TridDate') {
                                rla.Application_Date__c = convertedDate;
                                tridDateFound = true;
                                System.debug('Set Application_Date__c from TridDate: ' + convertedDate);
                            } else if (keyDate.name == 'UnderwrittenPreApproval') {
                                // STEP 2: For Purchase transactions, use UnderwrittenPreApproval keyDate (skip document check)
                                if (processPreApprovalDate) {
                                    rla.Pre_Approval_Date__c = convertedDate;
                                    System.debug('Set Pre_Approval_Date__c from UnderwrittenPreApproval keyDate: ' + convertedDate);
                                }
                                underwrittenPreApprovalFound = true;
                            } else if (keyDate.name == 'Underwriting') {
                                rla.Submission_Date__c = convertedDate;
                                underwritingFound = true;
                                System.debug('Set Submission_Date__c from Underwriting: ' + convertedDate);
                            } else if (keyDate.name == 'ApprovedDate') {
                                rla.Credit_Approval_Date__c = convertedDate;
                                rla.Conditional_Approval_Date__c = convertedDate;
                                approvedDateFound = true;
                                System.debug('Set Credit_Approval_Date__c and Conditional_Approval_Date__c from ApprovedDate: ' + convertedDate);
                            } else if (keyDate.name == 'ClearToCloseDate') {
                                rla.Clear_to_Close_Date__c = convertedDate;
                                clearToCloseDateFound = true;
                                System.debug('Set Clear_to_Close_Date__c from ClearToCloseDate: ' + convertedDate);
                            } else if (keyDate.name == 'EstimatedClosingDate') {
                                rla.Estimated_Closing_Date__c = convertedDate;
                                estimatedClosingDate = convertedDate; // Track for fallback logic
                                estimatedClosingDateFound = true;
                                System.debug('Set Estimated_Closing_Date__c from EstimatedClosingDate: ' + convertedDate);
                            } else if (keyDate.name == 'ClosingDate') {
                                rla.Closing_Date__c = convertedDate;
                                closingDateFound = true;
                                System.debug('Set Closing_Date__c from ClosingDate: ' + convertedDate);
                            } else if (keyDate.name == 'FundedDate') {
                                rla.Funded_Date__c = convertedDate;
                                fundedDate = convertedDate; // Track for fallback logic
                                fundedDateFound = true;
                                System.debug('Set Funded_Date__c from FundedDate: ' + convertedDate);
                            } else if (keyDate.name == 'EstimatedFundingDate') {
                                estimatedFundingDate = convertedDate; // Track for fallback logic
                                estimatedFundingDateFound = true;
                                System.debug('Found EstimatedFundingDate: ' + convertedDate);
                            } else if (keyDate.name == 'ClosingDisclosureSigned') {
                                rla.Initial_CD_Signature_Date__c = convertedDate;
                                closingDisclosureSignedFound = true;
                                System.debug('Set Initial_CD_Signature_Date__c from ClosingDisclosureSigned: ' + convertedDate);
                            } else if (keyDate.name == 'RateLockDate') {
                                rla.Lock_Date__c = convertedDate;
                                rateLockDateFound = true;
                                System.debug('Set Lock_Date__c from RateLockDate: ' + convertedDate);
                            } else if (keyDate.name == 'RateLockExpirationDate') {
                                rla.Lock_Expiration_Date__c = convertedDate;
                                lockExpirationDateFound = true;
                                rateLockExpirationDateFound = true;
                                
                                // Calculate Lock Status based on Rate Lock Expiration Date
                                if (convertedDate != null) {
                                    Date today = Date.today();
                                    if (convertedDate >= today) {
                                        rla.Lock_Status__c = 'Locked';
                                        System.debug('Rate Lock Expiration Date (' + convertedDate + ') is >= today (' + today + '), setting Lock_Status__c to "Locked"');
                                    } else {
                                        rla.Lock_Status__c = 'Expired';
                                        System.debug('Rate Lock Expiration Date (' + convertedDate + ') is < today (' + today + '), setting Lock_Status__c to "Expired"');
                                    }
                                } else {
                                    System.debug('Rate Lock Expiration Date is null, keeping default "Not Locked"');
                                }
                                System.debug('Set Lock_Expiration_Date__c from RateLockExpirationDate: ' + convertedDate);
                            } else if (keyDate.name == 'AppraisalContingencyDate') {
                                rla.Appraisal_Contingency_Date__c = convertedDate;
                                appraisalContingencyDateFound = true;
                                System.debug('Set Appraisal_Contingency_Date__c from AppraisalContingencyDate: ' + convertedDate);
                            } else if (keyDate.name == 'DisclosuresSent') {
                                rla.Disclosures_Sent_Date__c = convertedDate;
                                disclosuresSentFound = true;
                                System.debug('Set Disclosures_Sent_Date__c from DisclosuresSent: ' + convertedDate);
                            } else if (keyDate.name == 'FinalActionDate') {
                                rla.LoanSubStatusDate__c = convertedDate;
                                finalActionDateFound = true;
                                System.debug('Set LoanSubStatusDate__c from FinalActionDate: ' + convertedDate);
                            } else if (keyDate.name == 'ApplicationDate') {
                                // ApplicationDate from keyDates - no longer mapping to Ready_to_Register_Date__c
                                // Ready_to_Register_Date__c should come from ReadyToRegister milestone instead
                                applicationDateFound = true;
                                System.debug('Found ApplicationDate keyDate: ' + convertedDate + ' (not mapping to any field)');
                            } else if (keyDate.name == 'Processing') {
                                rla.Ready_to_Submit_Date__c = convertedDate;
                                processingFound = true;
                                System.debug('Set Ready_to_Submit_Date__c from Processing: ' + convertedDate);
                            } else if (keyDate.name == 'ReadyToRegisterDate') {
                                rla.Ready_to_Register_Date__c = convertedDate;
                                System.debug('Set Ready_to_Register_Date__c from ReadyToRegisterDate keyDate: ' + convertedDate);
                            } else if (keyDate.name == 'AppraisalReceivedDate') {
                                rla.Appraisal_Received_Date__c = convertedDate;
                                System.debug('Set Appraisal_Received_Date__c from AppraisalReceivedDate keyDate: ' + convertedDate);
                            } else if (keyDate.name == 'AppraisalOrderedDate') {
                                rla.Appraisal_Order_Date__c = convertedDate;
                                appraisalOrderedDateFound = true;
                                System.debug('Set Appraisal_Order_Date__c from AppraisalOrderedDate: ' + convertedDate);
                            } else if (keyDate.name == 'TitleReportOrderDate') {
                                rla.Title_Order_Date__c = convertedDate;
                                titleReportOrderDateFound = true;
                                System.debug('Set Title_Order_Date__c from TitleReportOrderDate: ' + convertedDate);
                            } else if (keyDate.name == 'ClosingDisclosureSent') {
                                rla.Closing_Disclosure_Send_Date__c = convertedDate;
                                closingDisclosureSentFound = true;
                                System.debug('Set Closing_Disclosure_Send_Date__c from ClosingDisclosureSent: ' + convertedDate);
                            } else if (keyDate.name == 'ContractDate') {
                                rla.Purchase_Contract_Date__c = convertedDate;
                                contractDateFound = true;
                                System.debug('Set Purchase_Contract_Date__c from ContractDate: ' + convertedDate);
                            }
                        }
                    }
                    
                    // Set fields to null if the corresponding key date was not found
                    if (!tridDateFound) {
                        rla.Application_Date__c = null;
                        System.debug('TridDate not found, setting Application_Date__c to null');
                    }
                    if (!underwrittenPreApprovalFound) {
                        // STEP 3: UnderwrittenPreApproval keyDate not found - check documents (only for Purchase transactions)
                        if (processPreApprovalDate) {
                            System.debug('UnderwrittenPreApproval keyDate not found, checking documents...');
                            Date preApprovalDateFromDocuments = extractPreApprovalDateFromDocuments(app.documents, applicationDate, app.creditReports);
                            
                            // STEP 4: Validate document date or set to null
                            if (preApprovalDateFromDocuments != null) {
                                rla.Pre_Approval_Date__c = preApprovalDateFromDocuments;
                                System.debug('Set Pre_Approval_Date__c from documents: ' + preApprovalDateFromDocuments);
                            } else {
                                rla.Pre_Approval_Date__c = null;
                                System.debug('No qualifying documents found, setting Pre_Approval_Date__c to null');
                            }
                        }
                    }
                    if (!underwritingFound) {
                        rla.Submission_Date__c = null;
                        System.debug('Underwriting not found, setting Submission_Date__c to null');
                    }
                    if (!approvedDateFound) {
                        rla.Credit_Approval_Date__c = null;
                        rla.Conditional_Approval_Date__c = null;
                        System.debug('ApprovedDate not found, setting Credit_Approval_Date__c and Conditional_Approval_Date__c to null');
                    }
                    if (!clearToCloseDateFound) {
                        rla.Clear_to_Close_Date__c = null;
                        System.debug('ClearToCloseDate not found, setting Clear_to_Close_Date__c to null');
                    }
                    if (!estimatedClosingDateFound) {
                        rla.Estimated_Closing_Date__c = null;
                        System.debug('EstimatedClosingDate not found, setting Estimated_Closing_Date__c to null');
                    }
                    if (!closingDateFound) {
                        rla.Closing_Date__c = null;
                        System.debug('ClosingDate not found, setting Closing_Date__c to null');
                    }
                    if (!fundedDateFound) {
                        rla.Funded_Date__c = null;
                        System.debug('FundedDate not found, setting Funded_Date__c to null');
                    }
                    if (!estimatedFundingDateFound) {
                        estimatedFundingDate = null;
                        System.debug('EstimatedFundingDate not found, setting to null');
                    }
                    if (!closingDisclosureSignedFound) {
                        rla.Initial_CD_Signature_Date__c = null;
                        System.debug('ClosingDisclosureSigned not found, setting Initial_CD_Signature_Date__c to null');
                    }
                    if (!rateLockDateFound) {
                        rla.Lock_Date__c = null;
                        System.debug('RateLockDate not found, setting Lock_Date__c to null');
                    }
                    if (!rateLockExpirationDateFound) {
                        rla.Lock_Expiration_Date__c = null;
                        System.debug('RateLockExpirationDate not found, setting Lock_Expiration_Date__c to null');
                    }
                    if (!appraisalContingencyDateFound) {
                        rla.Appraisal_Contingency_Date__c = null;
                        System.debug('AppraisalContingencyDate not found, setting Appraisal_Contingency_Date__c to null');
                    }
                    if (!disclosuresSentFound) {
                        rla.Disclosures_Sent_Date__c = null;
                        System.debug('DisclosuresSent not found, setting Disclosures_Sent_Date__c to null');
                    }
                    if (!finalActionDateFound) {
                        rla.LoanSubStatusDate__c = null;
                        System.debug('FinalActionDate not found, setting LoanSubStatusDate__c to null');
                    }
                    if (!applicationDateFound) {
                        // ApplicationDate keyDate no longer maps to Ready_to_Register_Date__c
                        // Ready_to_Register_Date__c is now handled by ReadyToRegister milestone
                        System.debug('ApplicationDate keyDate not found (not mapping to any field)');
                    }
                    if (!processingFound) {
                        rla.Ready_to_Submit_Date__c = null;
                        System.debug('Processing not found, setting Ready_to_Submit_Date__c to null');
                    }
                    if (!appraisalOrderedDateFound) {
                        rla.Appraisal_Order_Date__c = null;
                        System.debug('AppraisalOrderedDate not found, setting Appraisal_Order_Date__c to null');
                    }
                    if (!titleReportOrderDateFound) {
                        rla.Title_Order_Date__c = null;
                        System.debug('TitleReportOrderDate not found, setting Title_Order_Date__c to null');
                    }
                    if (!closingDisclosureSentFound) {
                        rla.Closing_Disclosure_Send_Date__c = null;
                        System.debug('ClosingDisclosureSent not found, setting Closing_Disclosure_Send_Date__c to null');
                    }
                    if (!contractDateFound) {
                        rla.Purchase_Contract_Date__c = null;
                        System.debug('ContractDate not found, setting Purchase_Contract_Date__c to null');
                    }
                    
                    // Fallback logic for Funded_Date__c:
                    // 1. If FundedDate has a value, use FundedDate (already handled above)
                    // 2. If FundedDate does not have a value, but EstimatedFundingDate has a value, use EstimatedFundingDate
                    // 3. If both FundedDate and EstimatedFundingDate do not have values, but EstimatedClosingDate has a value, use EstimatedClosingDate
                    // 4. Otherwise, leave as null
                    if (fundedDate == null) {
                        if (estimatedFundingDate != null) {
                            rla.Funded_Date__c = estimatedFundingDate;
                            System.debug('FundedDate was empty, using EstimatedFundingDate as fallback: ' + estimatedFundingDate);
                        } else if (estimatedClosingDate != null) {
                            rla.Funded_Date__c = estimatedClosingDate;
                            System.debug('FundedDate and EstimatedFundingDate were empty, using EstimatedClosingDate as fallback: ' + estimatedClosingDate);
                        } else {
                            System.debug('FundedDate, EstimatedFundingDate, and EstimatedClosingDate were all empty, leaving Funded_Date__c as null');
                        }
                    }
                    
                    // No rate lock expiration date was found, keeping default "Not Locked"
                    if (!lockExpirationDateFound) {
                        System.debug('No Rate Lock Expiration Date found in key dates, keeping default "Not Locked"');
                    }
                } else {
                    // No key dates available, set all date fields to null and keep default "Not Locked"
                    System.debug('No key dates available, setting most date fields to null');
                    rla.Application_Date__c = null;
                    
                    // STEP 3: No keyDates at all - check documents for Purchase transactions
                    if (processPreApprovalDate) {
                        System.debug('No keyDates found, checking documents for pre-approval date...');
                        Date preApprovalDateFromDocuments = extractPreApprovalDateFromDocuments(app.documents, applicationDate, app.creditReports);
                        
                        // STEP 4: Validate document date or set to null
                        if (preApprovalDateFromDocuments != null) {
                            rla.Pre_Approval_Date__c = preApprovalDateFromDocuments;
                            System.debug('No key dates, but setting Pre_Approval_Date__c from documents: ' + preApprovalDateFromDocuments);
                        } else {
                            rla.Pre_Approval_Date__c = null;
                            System.debug('No key dates and no document-based pre-approval date found');
                        }
                    } else {
                        System.debug('No key dates and transactionType is not Purchase, Pre_Approval_Date__c is null');
                    }
                    rla.Submission_Date__c = null;
                    rla.Credit_Approval_Date__c = null;
                    rla.Conditional_Approval_Date__c = null;
                    rla.Clear_to_Close_Date__c = null;
                    rla.Estimated_Closing_Date__c = null;
                    rla.Closing_Date__c = null;
                    rla.Funded_Date__c = null;
                    rla.Initial_CD_Signature_Date__c = null;
                    rla.Lock_Date__c = null;
                    rla.Lock_Expiration_Date__c = null;
                    rla.Appraisal_Contingency_Date__c = null;
                    rla.Disclosures_Sent_Date__c = null;
                    rla.LoanSubStatusDate__c = null;
                    rla.Ready_to_Register_Date__c = null;
                    rla.Ready_to_Submit_Date__c = null;
                    System.debug('All date fields set to null due to no key dates available');
                    
                    // Also ensure fallback variables are null when no key dates are available
                    System.debug('FundedDate, EstimatedFundingDate, and EstimatedClosingDate were all unavailable, Funded_Date__c remains null');
                }
                
                // Map team members information if available
                if (app.teamMembers != null && !app.teamMembers.isEmpty()) {
                    // Find the primary originator
                    String primaryOriginatorEmail = null;
                    String primaryProcessorEmail = null;
                    String internalMloEmail = null;
                    String loanOfficerAssistantEmail = null;
                    Boolean sonarHasProcessorTeamMember = false;
                    
                    // First pass: Look for primary team members and check for processor existence
                    for (TeamMember member : app.teamMembers) {
                        if (member.role == 'Originator' && member.primary == true) {
                            primaryOriginatorEmail = member.email;
                        } else if (member.role == 'Processor' && member.primary == true) {
                            primaryProcessorEmail = member.email;
                            sonarHasProcessorTeamMember = true;
                        } else if (member.role == 'Originator' && member.primary == false) {
                            internalMloEmail = member.email;
                        } else if (member.role == 'OriginatorAssistant') {
                            loanOfficerAssistantEmail = member.email;
                        } else if (member.role == 'Processor') {
                            // Non-primary processor also counts as having a processor team member
                            sonarHasProcessorTeamMember = true;
                        }
                    }
                    
                    // If no primary originator found, use the first originator
                    if (primaryOriginatorEmail == null) {
                        for (TeamMember member : app.teamMembers) {
                            if (member.role == 'Originator') {
                                primaryOriginatorEmail = member.email;
                                break;
                            }
                        }
                    }
                    
                    // If no primary processor found, use the first processor (if any processor exists)
                    if (primaryProcessorEmail == null && sonarHasProcessorTeamMember) {
                        for (TeamMember member : app.teamMembers) {
                            if (member.role == 'Processor') {
                                primaryProcessorEmail = member.email;
                                break;
                            }
                        }
                    }
                    
                    System.debug('Sonar has processor team member: ' + sonarHasProcessorTeamMember);
                    System.debug('Primary processor email from Sonar: ' + primaryProcessorEmail);
                    
                    rla.Loan_Officer_Email__c = primaryOriginatorEmail;
                    
                    // Processor Email Conflict Resolution Logic - Only update if Sonar has a processor team member
                    if (sonarHasProcessorTeamMember) {
                        System.debug('Sonar has processor team member - updating processor email');
                        String resolvedProcessorEmail = resolveProcessorEmailConflict(rla.Processor_Email__c, primaryProcessorEmail, true);
                        rla.Processor_Email__c = resolvedProcessorEmail;
                    } else {
                        System.debug('Sonar does not have processor team member - leaving Salesforce processor email unchanged');
                        // Do not update processor email - leave Salesforce value as-is
                    }
                    
                    rla.Internal_MLO_Email__c = internalMloEmail;
                    rla.Loan_Officer_Assistant_Email__c = loanOfficerAssistantEmail;
                    
                    System.debug('Team member mappings - Primary Originator: ' + primaryOriginatorEmail + 
                               ', Primary Processor: ' + rla.Processor_Email__c + 
                               ', Internal MLO: ' + internalMloEmail + 
                               ', Loan Officer Assistant: ' + loanOfficerAssistantEmail);
                }
                
                // Map contacts to RLA fields (Title Agent, Buyer Agent, Seller Agent)
                if (app.contacts != null && !app.contacts.isEmpty()) {
                    Contact titleAgent = findContactWithLowestRef(app.contacts, 'TitleAgent', null);
                    if (titleAgent != null) {
                        rla.Title_Company_Name__c = titleAgent.companyName;
                        rla.Title_Company_Email_Address__c = titleAgent.email;
                        System.debug('Mapped Title Agent - company: ' + titleAgent.companyName + ', email: ' + titleAgent.email);
                    } else {
                        rla.Title_Company_Name__c = null;
                        rla.Title_Company_Email_Address__c = null;
                        System.debug('No TitleAgent contact found, cleared Title_Company_Name__c and Title_Company_Email_Address__c');
                    }
                    
                    Contact buyerAgent = findContactWithLowestRef(app.contacts, 'RealEstateAgent', 'Buyer');
                    if (buyerAgent != null) {
                        rla.Buyer_Agent_Company__c = buyerAgent.companyName;
                        rla.Buyer_Agent_Email__c = buyerAgent.email;
                        rla.Buyer_Agent_Phone__c = buyerAgent.phone;
                        rla.Buyer_Agent_Name__c = buildFullName(buyerAgent.firstName, buyerAgent.lastName);
                        System.debug('Mapped Buyer Agent - company: ' + buyerAgent.companyName + ', name: ' + rla.Buyer_Agent_Name__c);
                    } else {
                        rla.Buyer_Agent_Company__c = null;
                        rla.Buyer_Agent_Email__c = null;
                        rla.Buyer_Agent_Phone__c = null;
                        rla.Buyer_Agent_Name__c = null;
                        rla.Buyer_s_Real_Estate_Agent__c = null;
                        System.debug('No RealEstateAgent/Buyer contact found, cleared Buyer Agent fields and Buyer_s_Real_Estate_Agent__c');
                    }
                    
                    Contact sellerAgent = findContactWithLowestRef(app.contacts, 'RealEstateAgent', 'Seller');
                    if (sellerAgent != null) {
                        rla.Seller_Agent_Company__c = sellerAgent.companyName;
                        rla.Seller_Agent_Email__c = sellerAgent.email;
                        rla.Seller_Agent_Phone__c = sellerAgent.phone;
                        rla.Seller_Agent_Name__c = buildFullName(sellerAgent.firstName, sellerAgent.lastName);
                        System.debug('Mapped Seller Agent - company: ' + sellerAgent.companyName + ', name: ' + rla.Seller_Agent_Name__c);
                    } else {
                        rla.Seller_Agent_Company__c = null;
                        rla.Seller_Agent_Email__c = null;
                        rla.Seller_Agent_Phone__c = null;
                        rla.Seller_Agent_Name__c = null;
                        rla.Seller_s_Real_Estate_Agent__c = null;
                        System.debug('No RealEstateAgent/Seller contact found, cleared Seller Agent fields and Seller_s_Real_Estate_Agent__c');
                    }
                } else {
                    rla.Title_Company_Name__c = null;
                    rla.Title_Company_Email_Address__c = null;
                    rla.Buyer_Agent_Company__c = null;
                    rla.Buyer_Agent_Email__c = null;
                    rla.Buyer_Agent_Phone__c = null;
                    rla.Buyer_Agent_Name__c = null;
                    rla.Buyer_s_Real_Estate_Agent__c = null;
                    rla.Seller_Agent_Company__c = null;
                    rla.Seller_Agent_Email__c = null;
                    rla.Seller_Agent_Phone__c = null;
                    rla.Seller_Agent_Name__c = null;
                    rla.Seller_s_Real_Estate_Agent__c = null;
                    System.debug('No contacts in payload, cleared all contact-related RLA fields');
                }
                
                // Process milestones in descending order
                if (app.milestones != null && !app.milestones.isEmpty()) {
                    System.debug('Processing milestones - count: ' + app.milestones.size());
                    System.debug('Raw milestones from API:');
                    for (Milestone m : app.milestones) {
                        System.debug('  Raw Milestone: ' + m.name + ', completed: ' + m.completed + ', completedDate: ' + m.completedDate + ', order: ' + m.order);
                    }
                    
                    // Process specific milestones for date mapping
                    Date applicationSubmittedDate = null;
                    Date payloadApplicationDate = null;
                    
                    // Get applicationDate from main payload
                    if (app.applicationDate != null) {
                        try {
                            payloadApplicationDate = Date.valueOf(app.applicationDate.split('T')[0]);
                            System.debug('Payload application date: ' + payloadApplicationDate);
                        } catch (Exception e) {
                            System.debug('Error converting payload applicationDate: ' + e.getMessage() + ' - Input: ' + app.applicationDate);
                        }
                    }
                    
                    for (Milestone milestone : app.milestones) {
                        if (milestone.name == 'ApplicationSubmitted' && milestone.completed == true && milestone.completedDate != null) {
                            applicationSubmittedDate = convertISOStringToDate(milestone.completedDate);
                            System.debug('ApplicationSubmitted milestone date: ' + applicationSubmittedDate);
                        } else if (milestone.name == 'ReadyToRegister' && milestone.completed == true && milestone.completedDate != null) {
                            Date readyToRegisterDate = convertISOStringToDate(milestone.completedDate);
                            if (readyToRegisterDate != null) {
                                rla.Ready_to_Register_Date__c = readyToRegisterDate;
                                System.debug('Set Ready_to_Register_Date__c from ReadyToRegister milestone: ' + readyToRegisterDate);
                            }
                        }
                    }
                    
                    // Compare payload applicationDate with ApplicationSubmitted milestone date
                    // Use whichever is earlier, or the available one if one is blank
                    if (payloadApplicationDate != null && applicationSubmittedDate != null) {
                        if (payloadApplicationDate <= applicationSubmittedDate) {
                            rla.Date_File_Started__c = payloadApplicationDate;
                            System.debug('Set Date_File_Started__c to payload applicationDate (earlier): ' + payloadApplicationDate + ' vs milestone: ' + applicationSubmittedDate);
                        } else {
                            rla.Date_File_Started__c = applicationSubmittedDate;
                            System.debug('Set Date_File_Started__c to ApplicationSubmitted milestone (earlier): ' + applicationSubmittedDate + ' vs payload: ' + payloadApplicationDate);
                        }
                    } else if (payloadApplicationDate != null) {
                        rla.Date_File_Started__c = payloadApplicationDate;
                        System.debug('Set Date_File_Started__c to payload applicationDate (milestone not available): ' + payloadApplicationDate);
                    } else if (applicationSubmittedDate != null) {
                        rla.Date_File_Started__c = applicationSubmittedDate;
                        System.debug('Set Date_File_Started__c to ApplicationSubmitted milestone (payload not available): ' + applicationSubmittedDate);
                    } else {
                        System.debug('Neither payload applicationDate nor ApplicationSubmitted milestone date available');
                    }
                    
                    // Sort milestones by order in descending order
                    List<Milestone> sortedMilestones = new List<Milestone>(app.milestones);
                    sortedMilestones.sort(new MilestoneComparator());
                    
                    System.debug('Sorted milestones:');
                    for (Milestone m : sortedMilestones) {
                        System.debug('  Sorted Milestone: ' + m.name + ', completed: ' + m.completed + ', order: ' + m.order);
                    }
                    
                    // Find the completed milestone with the highest order
                    Boolean foundCompletedMilestone = false;
                    for (Milestone milestone : sortedMilestones) {
                        System.debug('Checking milestone: ' + milestone.name + ', completed: ' + milestone.completed + ', order: ' + milestone.order);
                        if (milestone.completed == true) {
                            System.debug('Found completed milestone: ' + milestone.name + ', setting milestoneCurrentName__c');
                            rla.milestoneCurrentName__c = milestone.name;
                            System.debug('Setting milestoneCurrentName__c to: ' + milestone.name);
                            foundCompletedMilestone = true;
                            break;
                        }
                    }
                    
                    if (!foundCompletedMilestone) {
                        System.debug('No completed milestones found in the response');
                        // Set milestone to "Started" when no milestones are completed
                        rla.milestoneCurrentName__c = 'Started';
                        System.debug('Setting milestoneCurrentName__c to "Started" since no milestones are completed');
                    }
                } else {
                    System.debug('No milestones found in the response');
                }
                
                // Process credit reports if available
                if (app.creditReports != null && !app.creditReports.isEmpty()) {
                    List<Credit_Request__c> creditRequestsToUpsert = new List<Credit_Request__c>();
                    
                    for (CreditReport creditReport : app.creditReports) {
                        System.debug('Processing credit report with ref: ' + creditReport.creditReportRef);
                        
                        // First try to find existing record using both reference fields
                        List<Credit_Request__c> existingRequests = [
                            SELECT Id, Credit_Report_Reference__c, Document_Reference__c
                            FROM Credit_Request__c 
                            WHERE Credit_Report_Reference__c = :creditReport.creditReportRef
                            AND Document_Reference__c = :creditReport.documentRef
                        ];
                        
                        Credit_Request__c creditRequest;
                        if (!existingRequests.isEmpty()) {
                            creditRequest = existingRequests[0];
                            System.debug('Found existing credit request with ID: ' + creditRequest.Id);
                        } else {
                            creditRequest = new Credit_Request__c();
                            System.debug('Creating new credit request');
                        }
                        
                        // Set all fields explicitly
                        creditRequest.Residential_Loan_Application__c = rla.Id;
                        creditRequest.Credit_Report_Reference__c = creditReport.creditReportRef;
                        creditRequest.Document_Reference__c = creditReport.documentRef;
                        
                        // Handle timestamp conversion
                        if (creditReport.timestamp != null) {
                            try {
                                String timestampStr = creditReport.timestamp.replace('T', ' ').replace('Z', '');
                                creditRequest.Timestamp__c = DateTime.valueOf(timestampStr);
                                System.debug('Set timestamp to: ' + creditRequest.Timestamp__c);
                            } catch (Exception e) {
                                System.debug('Error converting timestamp: ' + e.getMessage());
                            }
                        }
                        
                        creditRequest.Type__c = creditReport.type;
                        creditRequest.Action__c = creditReport.action;
                        creditRequest.Status__c = creditReport.status;
                        creditRequest.Result__c = creditReport.result;
                        creditRequest.Credit_Bureaus__c = creditReport.bureaus != null ? String.join(creditReport.bureaus, ';') : null;
                        creditRequest.Credit_Report_Type__c = creditReport.borrowerRefs != null && creditReport.borrowerRefs.size() > 1 ? 'Joint' : 'Individual';
                        
                        System.debug('Credit Request values - Reference: ' + creditRequest.Credit_Report_Reference__c + 
                                   ', Document: ' + creditRequest.Document_Reference__c + 
                                   ', Timestamp: ' + creditRequest.Timestamp__c);
                        
                        creditRequestsToUpsert.add(creditRequest);
                    }
                    
                    // Upsert credit requests using both reference fields
                    if (!creditRequestsToUpsert.isEmpty()) {
                        try {
                            upsert creditRequestsToUpsert;
                            System.debug('Successfully upserted ' + creditRequestsToUpsert.size() + ' credit request records');
                        } catch (Exception e) {
                            System.debug('Error upserting credit requests: ' + e.getMessage());
                            // Don't throw the exception to avoid blocking the main loan processing
                        }
                    }
                }
                

                
                // Calculate total proposed monthly payment
                System.debug('=== TOTAL PAYMENT CALCULATION START ===');
                Decimal totalPayment = 0;
                
                // Add all payment components (use 0 if null)
                // First mortgage is Decimal
                Decimal firstMortgage = rla.Proposed_Payment_First_Mortgage__c != null ? rla.Proposed_Payment_First_Mortgage__c : 0;
                
                // String fields that need conversion to Decimal
                Decimal mortgageInsurance = (rla.Proposed_Payment_Mortgage_Insurance__c != null && rla.Proposed_Payment_Mortgage_Insurance__c != '') ? 
                    Decimal.valueOf(rla.Proposed_Payment_Mortgage_Insurance__c) : 0;
                Decimal propertyTaxes = (rla.Proposed_Payment_Property_Taxes__c != null && rla.Proposed_Payment_Property_Taxes__c != '') ? 
                    Decimal.valueOf(rla.Proposed_Payment_Property_Taxes__c) : 0;
                Decimal otherExpenses = rla.Proposed_Payment_Other_Expenses__c != null ? rla.Proposed_Payment_Other_Expenses__c : 0;
                Decimal homeownersInsurance = (rla.Proposed_Payment_Homeowners_Insurance__c != null && rla.Proposed_Payment_Homeowners_Insurance__c != '') ? 
                    Decimal.valueOf(rla.Proposed_Payment_Homeowners_Insurance__c) : 0;
                Decimal hoaDues = (rla.Proposed_Payment_HOA_Dues__c != null && rla.Proposed_Payment_HOA_Dues__c != '') ? 
                    Decimal.valueOf(rla.Proposed_Payment_HOA_Dues__c) : 0;
                
                totalPayment = mortgageInsurance + firstMortgage + propertyTaxes + otherExpenses + homeownersInsurance + hoaDues;
                
                rla.Total_Proposed_Monthly_Payment__c = totalPayment;
                System.debug('Total Payment Calculation:');
                System.debug('  Mortgage Insurance: ' + mortgageInsurance);
                System.debug('  First Mortgage: ' + firstMortgage);
                System.debug('  Property Taxes: ' + propertyTaxes);
                System.debug('  Other Expenses: ' + otherExpenses);
                System.debug('  Homeowners Insurance: ' + homeownersInsurance);
                System.debug('  HOA Dues: ' + hoaDues);
                System.debug('  Total: ' + totalPayment);
                System.debug('Set Total_Proposed_Monthly_Payment__c to: ' + totalPayment);
                System.debug('=== TOTAL PAYMENT CALCULATION END ===');
                
                // Debug final status before insert/update
                System.debug('Final status before DML - rla.Status__c: ' + rla.Status__c + ', app.loanStatus: ' + app.loanStatus);
                System.debug('Final milestone before DML - rla.milestoneCurrentName__c: ' + rla.milestoneCurrentName__c);
                System.debug('Record ID before DML: ' + rla.Id);
                
                // Insert or update the record
                if (rla.Id == null) {
                    System.debug('Attempting to insert new record');
                    System.debug('Record values - ApplicationExtIdentifier__c: ' + rla.ApplicationExtIdentifier__c + 
                               ', SonarLoanID__c: ' + rla.SonarLoanID__c + 
                               ', Sonar_GUID__c: ' + rla.Sonar_GUID__c +
                               ', Status__c: ' + rla.Status__c +
                               ', milestoneCurrentName__c: ' + rla.milestoneCurrentName__c);
                    try {
                        insert rla;
                        System.debug('Successfully inserted new record with ID: ' + rla.Id + ', Sonar_GUID__c: ' + rla.Sonar_GUID__c + ', Status__c: ' + rla.Status__c + ', milestoneCurrentName__c: ' + rla.milestoneCurrentName__c);
                    } catch (DMLException e) {
                        System.debug('Insert failed with DML Error: ' + e.getMessage());
                        System.debug('Error Type: ' + e.getTypeName());
                        System.debug('Error Fields: ' + e.getDmlFieldNames(0));
                        System.debug('Error Status Code: ' + e.getDmlStatusCode(0));
                        
                        // If it's a duplicate value error, try to find the existing record and update it instead
                        if (e.getDmlStatusCode(0) == StatusCode.DUPLICATE_VALUE.name()) {
                            System.debug('Duplicate value detected, attempting to find existing record for update');
                            // Try to find the record that caused the duplicate using Sonar_GUID__c
                            List<ResidentialLoanApplication__c> duplicateRecords = [
                                SELECT Id, Application_Date__c, ApplicationExtIdentifier__c, SonarLoanID__c, Sonar_GUID__c, milestoneCurrentName__c, Status__c, Date_File_Started__c,
                                       Proposed_Payment_First_Mortgage__c, Proposed_Payment_Mortgage_Insurance__c, Proposed_Payment_Property_Taxes__c,
                                       Proposed_Payment_Other_Expenses__c, Proposed_Payment_Homeowners_Insurance__c, Proposed_Payment_HOA_Dues__c,
                                       Total_Proposed_Monthly_Payment__c, Property_Address__c, Lender_Name__c
                                FROM ResidentialLoanApplication__c 
                                WHERE Sonar_GUID__c = :sonarGuid
                                LIMIT 1
                            ];
                            
                            // If not found by Sonar_GUID__c, try by individual fields as fallback
                            if (duplicateRecords.isEmpty() && app != null && app.externalId != null && app.loanId != null) {
                                System.debug('Duplicate not found by Sonar_GUID__c, trying individual fields');
                                duplicateRecords = [
                                    SELECT Id, Application_Date__c, ApplicationExtIdentifier__c, SonarLoanID__c, Sonar_GUID__c, milestoneCurrentName__c, Status__c, Date_File_Started__c,
                                           Proposed_Payment_First_Mortgage__c, Proposed_Payment_Mortgage_Insurance__c, Proposed_Payment_Property_Taxes__c,
                                           Proposed_Payment_Other_Expenses__c, Proposed_Payment_Homeowners_Insurance__c, Proposed_Payment_HOA_Dues__c,
                                           Total_Proposed_Monthly_Payment__c, Property_Address__c, Lender_Name__c
                                    FROM ResidentialLoanApplication__c 
                                    WHERE ApplicationExtIdentifier__c = :app.externalId 
                                    AND SonarLoanID__c = :app.loanId
                                    LIMIT 1
                                ];
                            }
                            
                            // Third level: If still not found, try by SonarLoanID__c only
                            if (duplicateRecords.isEmpty() && app != null && app.loanId != null) {
                                System.debug('Duplicate not found by individual fields, trying SonarLoanID__c only');
                                duplicateRecords = [
                                    SELECT Id, Application_Date__c, ApplicationExtIdentifier__c, SonarLoanID__c, Sonar_GUID__c, milestoneCurrentName__c, Status__c, Date_File_Started__c,
                                           Proposed_Payment_First_Mortgage__c, Proposed_Payment_Mortgage_Insurance__c, Proposed_Payment_Property_Taxes__c,
                                           Proposed_Payment_Other_Expenses__c, Proposed_Payment_Homeowners_Insurance__c, Proposed_Payment_HOA_Dues__c,
                                           Total_Proposed_Monthly_Payment__c, Property_Address__c, Lender_Name__c
                                    FROM ResidentialLoanApplication__c 
                                    WHERE SonarLoanID__c = :app.loanId
                                    LIMIT 1
                                ];
                            }
                            
                            if (!duplicateRecords.isEmpty()) {
                                rla = duplicateRecords[0];
                                System.debug('Found duplicate record with ID: ' + rla.Id + ', current milestoneCurrentName__c: ' + rla.milestoneCurrentName__c + ', proceeding with update');
                                
                                // If the duplicate record doesn't have Sonar_GUID__c populated, set it
                                if (rla.Sonar_GUID__c == null && sonarGuid != null) {
                                    rla.Sonar_GUID__c = sonarGuid;
                                    System.debug('Setting Sonar_GUID__c on duplicate record: ' + sonarGuid);
                                }
                                
                                update rla;
                                System.debug('Successfully updated duplicate record with milestoneCurrentName__c: ' + rla.milestoneCurrentName__c);
                            } else {
                                throw e; // Re-throw if we can't find the duplicate
                            }
                        } else {
                            throw e; // Re-throw non-duplicate errors
                        }
                    }
                } else {
                    System.debug('Attempting to update existing record: ' + rla.Id + ', Sonar_GUID__c: ' + rla.Sonar_GUID__c + ', Status__c: ' + rla.Status__c + ', milestoneCurrentName__c: ' + rla.milestoneCurrentName__c);
                    update rla;
                    System.debug('Successfully updated record with Sonar_GUID__c: ' + rla.Sonar_GUID__c + ', Status__c: ' + rla.Status__c + ', milestoneCurrentName__c: ' + rla.milestoneCurrentName__c);
                }
                
                // Update borrower mailing address on Contact (Borrower__c / Co_Borrower__c) from payload residences - after RLA has Id
                if (rla.Id != null && app.borrowers != null && !app.borrowers.isEmpty()) {
                    List<ResidentialLoanApplication__c> rlaWithLookups = [
                        SELECT Id, Borrower__c, Co_Borrower__c
                        FROM ResidentialLoanApplication__c
                        WHERE Id = :rla.Id
                        LIMIT 1
                    ];
                    if (!rlaWithLookups.isEmpty()) {
                        updateBorrowerMailingAddressFromResidences(rlaWithLookups[0], app.borrowers);
                    }
                }
                
                // Process notes for post-funding review completion - MUST happen after RLA record has an ID
                System.debug('=== NOTES PROCESSING START ===');
                Boolean postFundingReviewCompleted = false;
                
                if (app.notes != null && !app.notes.isEmpty()) {
                    System.debug('Processing ' + app.notes.size() + ' notes for post-funding review check');
                    
                    // Define search patterns (case insensitive)
                    List<String> searchPatterns = new List<String>{
                        'completed post closing review',
                        'completed post-closing review', 
                        'completed post funding review',
                        'completed post-funding review'
                    };
                    
                    for (Note note : app.notes) {
                        if (note.text != null && note.text.trim() != '') {
                            String noteTextLowerCase = note.text.toLowerCase();
                            System.debug('Checking note ID ' + note.loanNoteId + ': "' + note.text + '"');
                            
                            // Check if note text contains any of the search patterns
                            for (String pattern : searchPatterns) {
                                if (noteTextLowerCase.contains(pattern)) {
                                    postFundingReviewCompleted = true;
                                    System.debug('FOUND POST-FUNDING REVIEW COMPLETION in note ID ' + note.loanNoteId + 
                                               ' with pattern: "' + pattern + '"');
                                    break; // Exit inner loop once found
                                }
                            }
                            
                            if (postFundingReviewCompleted) {
                                break; // Exit outer loop once found
                            }
                        }
                    }
                } else {
                    System.debug('No notes found in loan data');
                }
                
                // Set the field based on findings
                rla.Initial_Post_Funding_Review_Completed__c = postFundingReviewCompleted;
                System.debug('Set Initial_Post_Funding_Review_Completed__c to: ' + postFundingReviewCompleted);
                
                // Set Manager_Post_Funding_Review_Completed__c based on Initial_Post_Funding_Review_Completed__c AND loanStatus
                Boolean managerPostFundingReviewCompleted = false;
                if (postFundingReviewCompleted && app.loanStatus != null && app.loanStatus.equalsIgnoreCase('Closed')) {
                    managerPostFundingReviewCompleted = true;
                    System.debug('Setting Manager_Post_Funding_Review_Completed__c to TRUE: postFundingReviewCompleted=' + postFundingReviewCompleted + 
                               ', loanStatus=' + app.loanStatus);
                } else {
                    System.debug('Setting Manager_Post_Funding_Review_Completed__c to FALSE: postFundingReviewCompleted=' + postFundingReviewCompleted + 
                               ', loanStatus=' + app.loanStatus);
                }
                rla.Manager_Post_Funding_Review_Completed__c = managerPostFundingReviewCompleted;
                System.debug('Set Manager_Post_Funding_Review_Completed__c to: ' + managerPostFundingReviewCompleted);
                
                // Update the record with the new field values
                try {
                    update rla;
                    System.debug('Successfully updated RLA record with post-funding review status');
                } catch (Exception e) {
                    System.debug('Error updating RLA record with post-funding review status: ' + e.getMessage());
                    // Don't throw the exception to avoid blocking other processing
                }
                
                System.debug('=== NOTES PROCESSING END ===');
                
                // Process fees if available - MUST happen after RLA record has an ID
                System.debug('=== FEE DATA AVAILABILITY CHECK ===');
                System.debug('app.fees is null: ' + (app.fees == null));
                System.debug('app.fees is empty: ' + (app.fees != null && app.fees.isEmpty()));
                System.debug('app.fees size: ' + (app.fees != null ? String.valueOf(app.fees.size()) : 'null'));
                
                if (app.fees != null && !app.fees.isEmpty()) {
                    List<Fee__c> feesToUpsert = new List<Fee__c>();
                    
                    System.debug('=== FEES PROCESSING START ===');
                    System.debug('Processing ' + app.fees.size() + ' fees for loan application ID: ' + rla.Id);
                    
                    // **DUPLICATE DETECTION WITHIN PAYLOAD** - Check for duplicate fee IDs in the same payload
                    Set<String> feeIdsInPayload = new Set<String>();
                    Map<String, Integer> feeIdCounts = new Map<String, Integer>();
                    
                    for (Fee fee : app.fees) {
                        if (fee.loanFeeId != null) {
                            String feeIdStr = String.valueOf(fee.loanFeeId);
                            feeIdsInPayload.add(feeIdStr);
                            
                            // Count occurrences
                            if (feeIdCounts.containsKey(feeIdStr)) {
                                feeIdCounts.put(feeIdStr, feeIdCounts.get(feeIdStr) + 1);
                            } else {
                                feeIdCounts.put(feeIdStr, 1);
                            }
                        }
                    }
                    
                    System.debug('=== PAYLOAD DUPLICATE ANALYSIS ===');
                    System.debug('Unique fee IDs in payload: ' + feeIdsInPayload.size());
                    System.debug('Total fees in payload: ' + app.fees.size());
                    
                    Boolean payloadHasDuplicates = false;
                    for (String feeId : feeIdCounts.keySet()) {
                        if (feeIdCounts.get(feeId) > 1) {
                            System.debug('DUPLICATE DETECTED IN PAYLOAD: Fee ID "' + feeId + '" appears ' + feeIdCounts.get(feeId) + ' times');
                            payloadHasDuplicates = true;
                        }
                    }
                    
                    if (!payloadHasDuplicates) {
                        System.debug('No duplicates detected within the payload');
                    }
                    System.debug('=== END PAYLOAD DUPLICATE ANALYSIS ===');
                    
                    // **WEBHOOK DUPLICATE DETECTION** - Add a timestamp to detect multiple webhook calls
                    System.debug('=== WEBHOOK TIMING ANALYSIS ===');
                    System.debug('Current processing time: ' + System.now());
                    System.debug('Loan ID being processed: ' + rla.SonarLoanID__c);
                    System.debug('RLA Record ID: ' + rla.Id);
                    System.debug('=== END WEBHOOK TIMING ANALYSIS ===');
                    
                    // First, let's see what existing fees we have for this loan
                    List<Fee__c> existingFeesForLoan = [
                        SELECT Id, Sonar_Fee_ID__c, Fee_Type__C, Total_Amount__c, Residential_Loan_Application__c, CreatedDate, LastModifiedDate
                        FROM Fee__c 
                        WHERE Residential_Loan_Application__c = :rla.Id
                        ORDER BY Sonar_Fee_ID__c, CreatedDate
                    ];
                    System.debug('Found ' + existingFeesForLoan.size() + ' existing fees for this loan:');
                    
                    // **EXISTING DUPLICATE ANALYSIS**
                    Map<String, List<Fee__c>> existingFeesByFeeId = new Map<String, List<Fee__c>>();
                    for (Fee__c existingFee : existingFeesForLoan) {
                        String feeId = String.valueOf(existingFee.Sonar_Fee_ID__c);
                        if (!existingFeesByFeeId.containsKey(feeId)) {
                            existingFeesByFeeId.put(feeId, new List<Fee__c>());
                        }
                        existingFeesByFeeId.get(feeId).add(existingFee);
                        
                        System.debug('  Existing Fee - ID: ' + existingFee.Id + ', Sonar_Fee_ID__c: ' + existingFee.Sonar_Fee_ID__c + 
                                   ', Fee_Type__C: ' + existingFee.Fee_Type__C + ', Total_Amount__c: ' + existingFee.Total_Amount__c +
                                   ', CreatedDate: ' + existingFee.CreatedDate + ', LastModifiedDate: ' + existingFee.LastModifiedDate);
                    }
                    
                    // Log existing duplicates
                    System.debug('=== EXISTING DUPLICATE ANALYSIS ===');
                    Boolean existingDuplicatesFound = false;
                    for (String feeId : existingFeesByFeeId.keySet()) {
                        List<Fee__c> feesWithSameId = existingFeesByFeeId.get(feeId);
                        if (feesWithSameId.size() > 1) {
                            System.debug('EXISTING DUPLICATE: Fee ID "' + feeId + '" has ' + feesWithSameId.size() + ' existing records');
                            for (Fee__c dupFee : feesWithSameId) {
                                System.debug('  Duplicate Record: ' + dupFee.Id + ', Amount: ' + dupFee.Total_Amount__c + ', Created: ' + dupFee.CreatedDate);
                            }
                            existingDuplicatesFound = true;
                        }
                    }
                    
                    if (!existingDuplicatesFound) {
                        System.debug('No existing duplicates found in database');
                    }
                    System.debug('=== END EXISTING DUPLICATE ANALYSIS ===');
                    
                    // Track processed fee IDs to prevent duplicates within this processing cycle
                    // **IMPORTANT**: Fee uniqueness is based ONLY on loanFeeId (External ID), NOT on feeType
                    // Multiple fees can have the same feeType (e.g., multiple "BrokerFee" records)
                    Set<String> processedFeeIds = new Set<String>();
                    
                    for (Fee fee : app.fees) {
                        System.debug('--- Processing individual fee ---');
                        System.debug('Fee from API - loanFeeId: ' + fee.loanFeeId + ', feeType: ' + fee.feeType + ', amount: ' + fee.amount);
                        System.debug('Fee additional details - section: ' + fee.section + ', description: ' + fee.description + ', paidTo: ' + fee.paidTo);
                        
                        // **SKIP FEES WITH NULL loanFeeId** - External ID field cannot be null
                        if (fee.loanFeeId == null) {
                            System.debug('SKIPPING FEE: loanFeeId is null - cannot create fee record without Sonar_Fee_ID__c (feeType: ' + fee.feeType + ')');
                            continue;
                        }
                        
                        // **SKIP DUPLICATE PROCESSING** - Skip if we've already processed this loanFeeId in this cycle
                        // Note: This is based on loanFeeId ONLY, not feeType - multiple fees can have same feeType
                        String currentFeeId = String.valueOf(fee.loanFeeId);
                        if (processedFeeIds.contains(currentFeeId)) {
                            System.debug('SKIPPING DUPLICATE: Fee ID "' + currentFeeId + '" already processed in this cycle (based on loanFeeId, not feeType)');
                            continue;
                        }
                        processedFeeIds.add(currentFeeId);
                        
                        // **ENHANCED DEBUGGING** - Let's see what we're searching for vs what exists
                        System.debug('=== SEARCHING FOR EXISTING FEE ===');
                        System.debug('Searching with Residential_Loan_Application__c: ' + rla.Id);
                        System.debug('Searching with Sonar_Fee_ID__c: "' + fee.loanFeeId + '"');
                        
                        // **EXTERNAL ID UPSERT APPROACH** - Let Salesforce handle the matching via External ID
                        // Create a new Fee__c record and let upsert determine if it's insert or update
                        Fee__c feeRecord = new Fee__c();
                        
                        // Check if this fee ID exists in our pre-analyzed data for logging purposes
                        List<Fee__c> existingFees = new List<Fee__c>();
                        if (existingFeesByFeeId.containsKey(currentFeeId)) {
                            existingFees = existingFeesByFeeId.get(currentFeeId);
                        }
                        
                        if (!existingFees.isEmpty()) {
                            if (existingFees.size() > 1) {
                                System.debug('MULTIPLE EXISTING RECORDS FOUND: ' + existingFees.size() + ' records for fee ID "' + currentFeeId + '"');
                                System.debug('External ID upsert will handle the matching automatically');
                                
                                // Log all the duplicates for awareness (External ID constraint should prevent future duplicates)
                                for (Integer i = 0; i < existingFees.size(); i++) {
                                    Fee__c dupRecord = existingFees[i];
                                    System.debug('  Duplicate #' + (i+1) + ': ID=' + dupRecord.Id + 
                                               ', Amount=' + dupRecord.Total_Amount__c + 
                                               ', LastModified=' + dupRecord.LastModifiedDate);
                                }
                            } else {
                                System.debug('EXISTING FEE RECORD FOUND:');
                                System.debug('  - ID: ' + existingFees[0].Id);
                                System.debug('  - Current Fee_Type__C: ' + existingFees[0].Fee_Type__C);
                                System.debug('  - Current Total_Amount__c: ' + existingFees[0].Total_Amount__c);
                                System.debug('  - Will update to amount: ' + fee.amount);
                            }
                        } else {
                            System.debug('NO EXISTING FEE RECORD FOUND - Will create new record for loanFeeId: ' + fee.loanFeeId);
                        }
                        
                        // Format the fee type name from CamelCase to Title Case
                        String formattedFeeType = formatFeeTypeName(fee.feeType);
                        
                        // Calculate Total_Amount__c as the sum of all individual amount fields
                        // This ensures data consistency and accuracy regardless of whether fee.amount is provided
                        Decimal totalAmount = 0.0;
                        if (fee.borrowerPacAmount != null) {
                            totalAmount += fee.borrowerPacAmount;
                        }
                        if (fee.borrowerPocAmount != null) {
                            totalAmount += fee.borrowerPocAmount;
                        }
                        if (fee.sellerPacAmount != null) {
                            totalAmount += fee.sellerPacAmount;
                        }
                        if (fee.sellerPocAmount != null) {
                            totalAmount += fee.sellerPocAmount;
                        }
                        if (fee.othersPaidAmount != null) {
                            totalAmount += fee.othersPaidAmount;
                        }
                        System.debug('Calculated Total_Amount__c from sum of all individual amount fields: ' + totalAmount + 
                                   ' (borrowerPac: ' + fee.borrowerPacAmount + ', borrowerPoc: ' + fee.borrowerPocAmount + 
                                   ', sellerPac: ' + fee.sellerPacAmount + ', sellerPoc: ' + fee.sellerPocAmount + 
                                   ', othersPaid: ' + fee.othersPaidAmount + ')');
                        
                        // Set all fields explicitly - always update all fields to ensure current data
                        feeRecord.Residential_Loan_Application__c = rla.Id;
                        feeRecord.Sonar_Fee_ID__c = fee.loanFeeId;
                        feeRecord.Fee_Type__C = formattedFeeType;
                        feeRecord.Fee_Section__c = fee.section;
                        feeRecord.Total_Amount__c = totalAmount;
                        feeRecord.Amount_Paid_Borrower_at_Closing__c = fee.borrowerPacAmount;
                        feeRecord.Amount_Paid_Borrower_Outside_of_Closing__c = fee.borrowerPocAmount;
                        feeRecord.Amount_Paid_Seller_at_Closing__c = fee.sellerPacAmount;
                        feeRecord.Amount_Paid_Seller_Outside_of_Closing__c = fee.sellerPocAmount;
                        feeRecord.Amount_Paid_by_Others__c = fee.othersPaidAmount;
                        feeRecord.Paid_To__c = fee.paidTo;
                        feeRecord.Paid_By__c = fee.paidBy;
                        feeRecord.Borrower_Can_Shop__c = fee.borrowerCanShop;
                        feeRecord.Borrower_Did_Shop__c = fee.borrowerDidShop;
                        feeRecord.Borrower_Did_Select__c = fee.borrowerDidSelect;
                        feeRecord.Include_in_APR__c = fee.includeInApr;
                        feeRecord.Can_Finance__c = fee.canFinance;
                        feeRecord.Refundable__c = fee.refundable;
                        feeRecord.Optional__c = fee.optional;
                        
                        System.debug('FINAL FEE RECORD VALUES:');
                        System.debug('  - Sonar_Fee_ID__c: ' + feeRecord.Sonar_Fee_ID__c);
                        System.debug('  - Fee_Type__C: ' + feeRecord.Fee_Type__C + ' (Original: ' + fee.feeType + ', Formatted: ' + formattedFeeType + ')');
                        System.debug('  - Total_Amount__c: ' + feeRecord.Total_Amount__c + ' (calculated from sum of all individual amount fields)');
                        System.debug('  - Amount_Paid_Borrower_at_Closing__c: ' + feeRecord.Amount_Paid_Borrower_at_Closing__c);
                        System.debug('  - Amount_Paid_Borrower_Outside_of_Closing__c: ' + feeRecord.Amount_Paid_Borrower_Outside_of_Closing__c);
                        System.debug('  - Amount_Paid_Seller_at_Closing__c: ' + feeRecord.Amount_Paid_Seller_at_Closing__c);
                        System.debug('  - Amount_Paid_Seller_Outside_of_Closing__c: ' + feeRecord.Amount_Paid_Seller_Outside_of_Closing__c);
                        System.debug('  - Amount_Paid_by_Others__c: ' + feeRecord.Amount_Paid_by_Others__c);
                        System.debug('  - Fee_Section__c: ' + feeRecord.Fee_Section__c);
                        System.debug('  - Paid_To__c: ' + feeRecord.Paid_To__c);
                        System.debug('  - Record ID: ' + feeRecord.Id + ' (null = new record)');
                        
                        feesToUpsert.add(feeRecord);
                    }
                    
                    // Upsert fee records - this will insert new records or update existing ones
                    if (!feesToUpsert.isEmpty()) {
                        try {
                            System.debug('=== ABOUT TO UPSERT ' + feesToUpsert.size() + ' FEE RECORDS ===');
                            
                            // Log what we're about to upsert
                            for (Integer i = 0; i < feesToUpsert.size(); i++) {
                                Fee__c feeToUpsert = feesToUpsert[i];
                                System.debug('Fee #' + (i+1) + ' to upsert:');
                                System.debug('  - ID: ' + feeToUpsert.Id + ' (' + (feeToUpsert.Id == null ? 'INSERT' : 'UPDATE') + ')');
                                System.debug('  - Sonar_Fee_ID__c: ' + feeToUpsert.Sonar_Fee_ID__c);
                                System.debug('  - Fee_Type__C: ' + feeToUpsert.Fee_Type__C);
                                System.debug('  - Total_Amount__c: ' + feeToUpsert.Total_Amount__c);
                            }
                            
                            // Use Database.upsert with allOrNone=false to get detailed results for each record
                            // This allows us to identify which specific fees succeeded or failed
                            Database.UpsertResult[] upsertResults = Database.upsert(feesToUpsert, Fee__c.Sonar_Fee_ID__c, false);
                            
                            System.debug('=== FEE UPSERT RESULTS (using External ID) ===');
                            Integer successCount = 0;
                            Integer errorCount = 0;
                            
                            for (Integer i = 0; i < upsertResults.size(); i++) {
                                Database.UpsertResult result = upsertResults[i];
                                Fee__c fee = feesToUpsert[i];
                                
                                if (result.isSuccess()) {
                                    successCount++;
                                    String operation = fee.Id != null ? 'Updated' : 'Created';
                                    System.debug('  ✓ ' + operation + ' fee: ID=' + result.getId() + 
                                               ', Sonar_Fee_ID__c=' + fee.Sonar_Fee_ID__c + 
                                               ', Fee_Type__C=' + fee.Fee_Type__C + 
                                               ', Total_Amount__c=' + fee.Total_Amount__c);
                                } else {
                                    errorCount++;
                                    System.debug('  ✗ ERROR with fee (Sonar_Fee_ID__c=' + fee.Sonar_Fee_ID__c + 
                                               ', Fee_Type__C=' + fee.Fee_Type__C + '):');
                                    for (Database.Error error : result.getErrors()) {
                                        System.debug('    - ' + error.getMessage() + ' (Fields: ' + String.join(error.getFields(), ', ') + ')');
                                        System.debug('    - Status Code: ' + error.getStatusCode());
                                    }
                                }
                            }
                            
                            System.debug('=== FEE UPSERT SUMMARY ===');
                            System.debug('Total fees processed: ' + feesToUpsert.size());
                            System.debug('Successful: ' + successCount);
                            System.debug('Failed: ' + errorCount);
                            
                            // Query the database to verify the records were actually updated
                            List<Fee__c> verificationQuery = [
                                SELECT Id, Sonar_Fee_ID__c, Fee_Type__C, Total_Amount__c
                                FROM Fee__c 
                                WHERE Residential_Loan_Application__c = :rla.Id
                                ORDER BY Sonar_Fee_ID__c
                            ];
                            System.debug('=== VERIFICATION QUERY RESULTS (' + verificationQuery.size() + ' fees in database) ===');
                            for (Fee__c verifiedFee : verificationQuery) {
                                System.debug('DB Fee - ID: ' + verifiedFee.Id + ', Sonar_Fee_ID__c: ' + verifiedFee.Sonar_Fee_ID__c + 
                                           ', Fee_Type__C: ' + verifiedFee.Fee_Type__C + ', Total_Amount__c: ' + verifiedFee.Total_Amount__c);
                            }
                            
                        } catch (Exception e) {
                            System.debug('=== CRITICAL ERROR UPSERTING FEE RECORDS ===');
                            System.debug('Error message: ' + e.getMessage());
                            System.debug('Error stack trace: ' + e.getStackTraceString());
                            System.debug('Error type: ' + e.getTypeName());
                            // Don't throw the exception to avoid blocking the main loan processing
                        }
                    } else {
                        System.debug('No fees to upsert (feesToUpsert list is empty)');
                    }
                    System.debug('=== FEES PROCESSING END ===');
                } else {
                    System.debug('=== NO FEE DATA FOUND ===');
                    System.debug('No fees found in the loan application data (app.fees is null or empty)');
                    System.debug('This could indicate:');
                    System.debug('1. This loan truly has no fees configured in Sonar');
                    System.debug('2. Sonar webhook payload does not include fee data for this event type');
                    System.debug('3. Fee changes may require separate webhook configuration in Sonar');
                    System.debug('=== END NO FEE DATA ===');
                }
                
                // Process conditions if available - MUST happen after RLA record has an ID
                List<NormalizedConditionPayload> incomingConditions = buildNormalizedConditions(app.conditions, loanDataJson);
                System.debug('=== CONDITION DATA AVAILABILITY CHECK ===');
                System.debug('app.conditions is null: ' + (app.conditions == null));
                System.debug('app.conditions is empty: ' + (app.conditions != null && app.conditions.isEmpty()));
                System.debug('app.conditions size: ' + (app.conditions != null ? String.valueOf(app.conditions.size()) : 'null'));
                System.debug('normalized incomingConditions size: ' + incomingConditions.size());
                
                if (!incomingConditions.isEmpty()) {
                    List<Condition__c> conditionsToUpsert = new List<Condition__c>();
                    String normalizedPayloadLoanId = normalizeToString(app.loanId);
                    
                    System.debug('=== CONDITIONS PROCESSING START ===');
                    System.debug('Processing ' + incomingConditions.size() + ' conditions for loan application ID: ' + rla.Id);
                    
                    // Create a set to track unique condition keys (loanId + loanConditionId) for duplicate detection
                    Set<String> conditionKeysInPayload = new Set<String>();
                    Map<String, Integer> conditionKeyCounts = new Map<String, Integer>();
                    
                    for (NormalizedConditionPayload condition : incomingConditions) {
                        if (String.isNotBlank(condition.loanConditionId)) {
                            String conditionKey = (String.isNotBlank(normalizedPayloadLoanId) ? normalizedPayloadLoanId : '') + '_' + condition.loanConditionId;
                            conditionKeysInPayload.add(conditionKey);
                            
                            // Count occurrences
                            if (conditionKeyCounts.containsKey(conditionKey)) {
                                conditionKeyCounts.put(conditionKey, conditionKeyCounts.get(conditionKey) + 1);
                            } else {
                                conditionKeyCounts.put(conditionKey, 1);
                            }
                        }
                    }
                    
                    System.debug('=== CONDITION PAYLOAD DUPLICATE ANALYSIS ===');
                    System.debug('Unique condition keys in payload: ' + conditionKeysInPayload.size());
                    System.debug('Total conditions in payload: ' + incomingConditions.size());
                    
                    Boolean payloadHasDuplicateConditions = false;
                    for (String conditionKey : conditionKeyCounts.keySet()) {
                        if (conditionKeyCounts.get(conditionKey) > 1) {
                            System.debug('DUPLICATE CONDITION DETECTED IN PAYLOAD: Key "' + conditionKey + '" appears ' + conditionKeyCounts.get(conditionKey) + ' times');
                            payloadHasDuplicateConditions = true;
                        }
                    }
                    
                    if (!payloadHasDuplicateConditions) {
                        System.debug('No duplicate conditions detected within the payload');
                    }
                    System.debug('=== END CONDITION PAYLOAD DUPLICATE ANALYSIS ===');
                    
                    // High-signal diagnostics for condition sync (use ERROR level so it appears even when DEBUG logs are filtered)
                    System.debug(LoggingLevel.ERROR, 'COND_SYNC START | loanId=' + app.loanId + ' | rlaId=' + rla.Id +
                                ' | payloadConditionCount=' + incomingConditions.size());

                    Set<String> incomingConditionIds = new Set<String>();
                    for (NormalizedConditionPayload condition : incomingConditions) {
                        if (String.isNotBlank(condition.loanConditionId)) {
                            incomingConditionIds.add(condition.loanConditionId);
                        }
                    }

                    // Get existing conditions for this loan to check for existing records
                    List<Condition__c> existingConditionsForLoan = [
                        SELECT Id, Sonar_Condition_ID__c, Sonar_Borrower_Reference_ID__c, Sonar_Loan_ID__c, Description__c, Scope__c, 
                               Title__c, Type__c, Completed__c, Critical__c, Residential_Loan_Application__c, 
                               CreatedDate, LastModifiedDate
                        FROM Condition__c 
                        WHERE Sonar_Condition_ID__c IN :incomingConditionIds
                        AND (
                            Sonar_Loan_ID__c = :normalizedPayloadLoanId
                            OR (Sonar_Loan_ID__c = null AND Residential_Loan_Application__c = :rla.Id)
                        )
                        ORDER BY Sonar_Condition_ID__c, CreatedDate
                    ];
                    System.debug('Found ' + existingConditionsForLoan.size() + ' existing conditions for this loan');
                    System.debug(LoggingLevel.ERROR, 'COND_SYNC Existing condition count for rlaId=' + rla.Id + ' is ' + existingConditionsForLoan.size());
                    
                    // Create a map of existing conditions by their key (loanId + loanConditionId)
                    Map<String, Condition__c> existingConditionsByKey = new Map<String, Condition__c>();
                    Map<String, Integer> existingConditionKeyCounts = new Map<String, Integer>();
                    for (Condition__c existingCondition : existingConditionsForLoan) {
                        if (existingCondition.Sonar_Condition_ID__c != null) {
                            String existingLoanConditionId = existingCondition.Sonar_Condition_ID__c != null ? existingCondition.Sonar_Condition_ID__c.trim() : null;
                            String existingLoanId = String.isNotBlank(existingCondition.Sonar_Loan_ID__c) ? existingCondition.Sonar_Loan_ID__c.trim() : normalizedPayloadLoanId;
                            String conditionKey = (String.isNotBlank(existingLoanId) ? existingLoanId : '') + '_' + existingLoanConditionId;
                            existingConditionsByKey.put(conditionKey, existingCondition);
                            existingConditionKeyCounts.put(conditionKey, (existingConditionKeyCounts.containsKey(conditionKey) ? existingConditionKeyCounts.get(conditionKey) : 0) + 1);
                            
                            System.debug('  Existing Condition - Key: ' + conditionKey + ', ID: ' + existingCondition.Id + 
                                       ', Scope: ' + existingCondition.Scope__c + ', Type: ' + existingCondition.Type__c +
                                       ', Completed: ' + existingCondition.Completed__c + ', Critical: ' + existingCondition.Critical__c);
                        }
                    }

                    Integer duplicateExistingKeyCount = 0;
                    for (String existingKey : existingConditionKeyCounts.keySet()) {
                        if (existingConditionKeyCounts.get(existingKey) > 1) {
                            duplicateExistingKeyCount++;
                            System.debug(LoggingLevel.ERROR, 'COND_SYNC Existing duplicate key detected | key=' + existingKey +
                                        ' | count=' + existingConditionKeyCounts.get(existingKey));
                        }
                    }
                    System.debug(LoggingLevel.ERROR, 'COND_SYNC Existing key summary | uniqueKeys=' + existingConditionsByKey.size() +
                                ' | duplicateKeyCount=' + duplicateExistingKeyCount);
                    
                    // Process each condition from the payload (loanConditionId required; borrowerRef may be null for Borrower-scope conditions)
                    Integer payloadMissingLoanConditionIdCount = 0;
                    Integer payloadCompletedTrueCount = 0;
                    Integer payloadWithBorrowerRefCount = 0;
                    Integer payloadWithoutBorrowerRefCount = 0;
                    Integer matchedExistingCount = 0;
                    Integer newConditionCount = 0;
                    Integer completedTrueMissingExistingMatchCount = 0;
                    List<String> completedTrueMissingExistingKeys = new List<String>();

                    for (NormalizedConditionPayload condition : incomingConditions) {
                        String normalizedLoanConditionId = condition.loanConditionId != null ? condition.loanConditionId.trim() : null;
                        String normalizedBorrowerRef = String.isNotBlank(condition.borrowerRef) ? condition.borrowerRef.trim() : null;

                        if (String.isNotBlank(normalizedLoanConditionId)) {
                            String conditionKey = (String.isNotBlank(normalizedPayloadLoanId) ? normalizedPayloadLoanId : '') + '_' + normalizedLoanConditionId;

                            if (String.isNotBlank(normalizedBorrowerRef)) {
                                payloadWithBorrowerRefCount++;
                            } else {
                                payloadWithoutBorrowerRefCount++;
                            }
                            if (condition.completed == true) {
                                payloadCompletedTrueCount++;
                            }
                            
                            System.debug('Processing condition with key: ' + conditionKey);
                            System.debug('  loanConditionId: ' + normalizedLoanConditionId);
                            System.debug('  borrowerRef: ' + normalizedBorrowerRef);
                            System.debug('  name: ' + condition.name);
                            System.debug('  description: ' + condition.description);
                            System.debug('  scope: ' + condition.scope);
                            System.debug('  type: ' + condition.type);
                            System.debug('  completed: ' + condition.completed);
                            System.debug('  critical: ' + condition.critical);
                            
                            Condition__c conditionRecord;
                            
                            // Check if a record already exists with this key
                            if (existingConditionsByKey.containsKey(conditionKey)) {
                                // Update existing record
                                conditionRecord = existingConditionsByKey.get(conditionKey);
                                matchedExistingCount++;
                                System.debug('Updating existing condition record: ' + conditionRecord.Id);
                            } else {
                                // Create new record
                                conditionRecord = new Condition__c();
                                newConditionCount++;
                                conditionRecord.Residential_Loan_Application__c = rla.Id;
                                conditionRecord.Sonar_Condition_ID__c = normalizedLoanConditionId;
                                conditionRecord.Sonar_Borrower_Reference_ID__c = String.isNotBlank(normalizedBorrowerRef) ? normalizedBorrowerRef : null;
                                System.debug('Creating new condition record for key: ' + conditionKey);
                                if (condition.completed == true) {
                                    completedTrueMissingExistingMatchCount++;
                                    if (completedTrueMissingExistingKeys.size() < 20) {
                                        completedTrueMissingExistingKeys.add(conditionKey);
                                    }
                                }
                            }

                            // Always map Sonar loan id and borrower ref on condition records (borrower ref is supplemental, not identity)
                            conditionRecord.Sonar_Loan_ID__c = normalizedPayloadLoanId;
                            conditionRecord.Sonar_Borrower_Reference_ID__c = String.isNotBlank(normalizedBorrowerRef) ? normalizedBorrowerRef : null;
                            
                            // Map the fields (truncate to avoid DML errors from field length limits)
                            // The name field from Sonar should always map to Title__c
                            conditionRecord.Title__c = truncateToLength(condition.name, 255);
                            
                            // Map description if provided, otherwise set to null
                            if (String.isNotBlank(condition.description)) {
                                conditionRecord.Description__c = truncateToLength(condition.description, 32768);
                            } else {
                                conditionRecord.Description__c = null;
                            }
                            
                            conditionRecord.Scope__c = condition.scope;
                            conditionRecord.Type__c = condition.type;
                            conditionRecord.Completed__c = condition.completed;
                            conditionRecord.Critical__c = condition.critical;
                            
                            conditionsToUpsert.add(conditionRecord);
                        } else {
                            payloadMissingLoanConditionIdCount++;
                            System.debug('Skipping condition with missing loanConditionId - loanConditionId: ' + 
                                       condition.loanConditionId + ', borrowerRef: ' + condition.borrowerRef);
                        }
                    }

                    System.debug(LoggingLevel.ERROR,
                        'COND_SYNC Payload summary | total=' + incomingConditions.size() +
                        ' | completedTrue=' + payloadCompletedTrueCount +
                        ' | withBorrowerRef=' + payloadWithBorrowerRefCount +
                        ' | withoutBorrowerRef=' + payloadWithoutBorrowerRefCount +
                        ' | missingLoanConditionId=' + payloadMissingLoanConditionIdCount +
                        ' | matchedExisting=' + matchedExistingCount +
                        ' | newRecords=' + newConditionCount +
                        ' | toUpsert=' + conditionsToUpsert.size()
                    );
                    if (!completedTrueMissingExistingKeys.isEmpty()) {
                        System.debug(LoggingLevel.ERROR, 'COND_SYNC completed=true but no existing key match (first ' +
                                    completedTrueMissingExistingKeys.size() + '): ' + String.join(completedTrueMissingExistingKeys, ','));
                    }
                    System.debug(LoggingLevel.ERROR, 'COND_SYNC key ' + normalizedPayloadLoanId + '_32228 existsInPayload=' + conditionKeysInPayload.contains(normalizedPayloadLoanId + '_32228') +
                                ' | existsInExistingMap=' + existingConditionsByKey.containsKey(normalizedPayloadLoanId + '_32228'));
                    
                    // Upsert the conditions
                    if (!conditionsToUpsert.isEmpty()) {
                        try {
                            System.debug('About to upsert ' + conditionsToUpsert.size() + ' condition records');
                            
                            // Create an external ID field by combining Sonar_Condition_ID__c and Sonar_Borrower_Reference_ID__c
                            // Since we don't have an external ID field that combines both, we'll use regular upsert logic
                            Database.UpsertResult[] upsertResults = Database.upsert(conditionsToUpsert, false);
                            
                            System.debug('Condition upsert completed. Results:');
                            Integer successCount = 0;
                            Integer errorCount = 0;
                            
                            for (Integer i = 0; i < upsertResults.size(); i++) {
                                Database.UpsertResult result = upsertResults[i];
                                Condition__c condition = conditionsToUpsert[i];
                                
                                if (result.isSuccess()) {
                                    successCount++;
                                    String operation = condition.Id != null ? 'Updated' : 'Created';
                                    System.debug('  ' + operation + ' condition: ' + result.getId() + 
                                               ' (Key: ' + condition.Sonar_Condition_ID__c + '_' + condition.Sonar_Borrower_Reference_ID__c + ')');
                                } else {
                                    errorCount++;
                                    System.debug('  Error with condition (Key: ' + condition.Sonar_Condition_ID__c + '_' + condition.Sonar_Borrower_Reference_ID__c + '):');
                                    for (Database.Error error : result.getErrors()) {
                                        System.debug('    ' + error.getMessage());
                                    }
                                }
                            }
                            
                            System.debug('Condition upsert summary: ' + successCount + ' successful, ' + errorCount + ' errors');
                            System.debug(LoggingLevel.ERROR, 'COND_SYNC upsert summary | success=' + successCount + ' | errors=' + errorCount);
                            
                        } catch (Exception e) {
                            System.debug('Error upserting conditions: ' + e.getMessage());
                            System.debug('Error Type: ' + e.getTypeName());
                            System.debug('Error Stack Trace: ' + e.getStackTraceString());
                            System.debug(LoggingLevel.ERROR, 'COND_SYNC upsert exception | type=' + e.getTypeName() + ' | message=' + e.getMessage());
                            // Don't throw the exception to avoid blocking the main loan processing
                        }
                    } else {
                        System.debug('No conditions to upsert (conditionsToUpsert list is empty)');
                        System.debug(LoggingLevel.ERROR, 'COND_SYNC no-op | conditionsToUpsert is empty');
                    }
                    System.debug('=== CONDITIONS PROCESSING END ===');
                } else {
                    System.debug('=== NO CONDITION DATA FOUND ===');
                    System.debug('No usable conditions found in the loan application data');
                    System.debug('This could indicate:');
                    System.debug('1. This loan truly has no conditions configured in Sonar');
                    System.debug('2. Sonar webhook payload does not include condition data for this event type');
                    System.debug('3. Condition data is present but missing usable loanConditionId values');
                    System.debug('=== END NO CONDITION DATA ===');
                }
                
                // If estimatedValue is null or 0, calculate purchase price from loan amount and down payment
                if (app.subjectProperty == null || app.subjectProperty.estimatedValue == null || app.subjectProperty.estimatedValue == 0) {
                    Decimal loanAmount = app.loanAmount != null ? app.loanAmount : 0;
                    Decimal downPayment = app.downPayment != null ? app.downPayment : 0;
                    rla.Property_Value__c = loanAmount + downPayment;
                } else {
                    rla.Property_Value__c = app.subjectProperty.estimatedValue;
                }
                
            } catch (DMLException e) {
                System.debug('DML Error: ' + e.getMessage());
                System.debug('Error Type: ' + e.getTypeName());
                System.debug('Error Fields: ' + e.getDmlFieldNames(0));
                System.debug('Error Status Code: ' + e.getDmlStatusCode(0));
                throw e;
            } catch (Exception e) {
                System.debug('General Error: ' + e.getMessage());
                System.debug('Error Type: ' + e.getTypeName());
                throw e;
            }
            
        } catch (Exception e) {
            System.debug('Error processing loan data: ' + e.getMessage());
            throw e;
        }
    }

    // Helper method to find the contact with the lowest contactRef matching contactType and optional role
    private static Contact findContactWithLowestRef(List<Contact> contacts, String contactType, String role) {
        if (contacts == null || contacts.isEmpty() || contactType == null || contactType == '') {
            return null;
        }
        Contact result = null;
        Integer lowestRef = null;
        for (Contact c : contacts) {
            if (c == null || c.contactType == null || !c.contactType.equals(contactType)) {
                continue;
            }
            if (role != null && (c.role == null || !c.role.equals(role))) {
                continue;
            }
            Integer refNum = null;
            try {
                if (c.contactRef != null && c.contactRef.trim() != '') {
                    refNum = Integer.valueOf(c.contactRef.trim());
                }
            } catch (Exception e) {
                // If contactRef is not numeric, treat as highest (skip or use string compare)
                continue;
            }
            if (refNum != null && (lowestRef == null || refNum < lowestRef)) {
                lowestRef = refNum;
                result = c;
            }
        }
        return result;
    }

    // Helper method to build full name from first and last name
    private static String buildFullName(String firstName, String lastName) {
        List<String> parts = new List<String>();
        if (firstName != null && firstName.trim() != '') {
            parts.add(firstName.trim());
        }
        if (lastName != null && lastName.trim() != '') {
            parts.add(lastName.trim());
        }
        return parts.isEmpty() ? null : String.join(parts, ' ');
    }

    // Helper: update Contact mailing address from payload borrower residence (type contains "Mailing", most recent moveInDate)
    private static void updateBorrowerMailingAddressFromResidences(ResidentialLoanApplication__c rla, List<Borrower> borrowers) {
        if (rla == null || borrowers == null || borrowers.isEmpty()) {
            return;
        }
        List<Id> contactIds = new List<Id>();
        if (rla.Borrower__c != null) {
            contactIds.add(rla.Borrower__c);
        }
        if (rla.Co_Borrower__c != null && rla.Co_Borrower__c != rla.Borrower__c) {
            contactIds.add(rla.Co_Borrower__c);
        }
        for (Id contactId : contactIds) {
            updateContactMailingFromBorrowers(contactId, borrowers);
        }
    }

    // Single-Contact version; used by handler and exposed for tests to verify mailing-address logic without RLA lookup.
    @TestVisible
    private static void updateContactMailingFromBorrowers(Id contactId, List<Borrower> borrowers) {
        if (contactId == null || borrowers == null || borrowers.isEmpty()) {
            return;
        }
        try {
            List<SObject> contactsFromDb = [SELECT Id, FirstName, LastName FROM Contact WHERE Id = :contactId LIMIT 1];
            if (contactsFromDb.isEmpty()) {
                return;
            }
            SObject row = contactsFromDb[0];
            String conFirst = (String)row.get('FirstName');
            String conLast = (String)row.get('LastName');
            String conFirstNorm = conFirst != null ? conFirst.trim() : '';
            String conLastNorm = conLast != null ? conLast.trim() : '';
            Borrower matchedBorrower = null;
            for (Borrower b : borrowers) {
                String bFirst = b.firstName != null ? b.firstName.trim() : '';
                String bLast = b.lastName != null ? b.lastName.trim() : '';
                if (bFirst.equals(conFirstNorm) && bLast.equals(conLastNorm)) {
                    matchedBorrower = b;
                    break;
                }
            }
            if (matchedBorrower == null || matchedBorrower.residences == null || matchedBorrower.residences.isEmpty()) {
                return;
            }
            Residence mailingResidence = findMostRecentMailingResidence(matchedBorrower.residences);
            if (mailingResidence == null) {
                return;
            }
            SObject toUpdate = Schema.getGlobalDescribe().get('Contact').newSObject(contactId);
            toUpdate.put('MailingStreet', mailingResidence.street1);
            toUpdate.put('MailingCity', mailingResidence.city);
            toUpdate.put('MailingState', stateAbbreviationToFullName(mailingResidence.state));
            toUpdate.put('MailingPostalCode', mailingResidence.zipCode);
            update toUpdate;
            System.debug('Updated mailing address for Contact ' + contactId + ' from borrower ' + conFirstNorm + ' ' + conLastNorm);
        } catch (Exception e) {
            System.debug('Error updating Contact mailing address from borrowers: ' + e.getMessage());
            System.debug('Error Type: ' + e.getTypeName());
            if (Test.isRunningTest()) {
                throw e;
            }
        }
    }

    // Helper: from residences list, return the one with type containing "Mailing" and most recent moveInDate (nulls last)
    private static Residence findMostRecentMailingResidence(List<Residence> residences) {
        if (residences == null || residences.isEmpty()) {
            return null;
        }
        List<Residence> mailingOnly = new List<Residence>();
        for (Residence r : residences) {
            if (r != null && r.type != null && r.type.contains('Mailing')) {
                mailingOnly.add(r);
            }
        }
        if (mailingOnly.isEmpty()) {
            return null;
        }
        // Sort by moveInDate descending, nulls last
        mailingOnly.sort(new ResidenceMoveInDateComparator());
        return mailingOnly[0];
    }

    // Comparator for Residence: moveInDate descending, nulls last
    private class ResidenceMoveInDateComparator implements System.Comparator<Residence> {
        public Integer compare(Residence r1, Residence r2) {
            Date d1 = parseMoveInDate(r1.moveInDate);
            Date d2 = parseMoveInDate(r2.moveInDate);
            if (d1 == null && d2 == null) return 0;
            if (d1 == null) return 1;  // nulls last
            if (d2 == null) return -1;
            // descending (most recent first): d2 > d1 -> -1, d2 < d1 -> 1
            if (d2 > d1) return -1;
            if (d2 < d1) return 1;
            return 0;
        }
    }

    private static Date parseMoveInDate(String moveInDate) {
        if (moveInDate == null || moveInDate.trim() == '') {
            return null;
        }
        try {
            return Date.valueOf(moveInDate.split('T')[0]);
        } catch (Exception e) {
            return null;
        }
    }

    // Helper method to check if a pre-approval document exists
    private static Boolean hasPreApprovalDocument(List<Document> documents) {
        try {
            System.debug('=== CHECKING FOR PRE-APPROVAL DOCUMENT EXISTENCE ===');
            
            if (documents == null || documents.isEmpty()) {
                System.debug('No documents provided');
                return false;
            }
            
            System.debug('Checking ' + documents.size() + ' documents for pre-approval document');
            
            for (Document doc : documents) {
                boolean isPreApprovalDocument = false;
                
                // Check if filename contains "preapproval" (normalized to handle variations like "Pre-Approval", "Pre Approval", etc.)
                if (doc.filename != null) {
                    String normalizedFilename = doc.filename.toLowerCase().replaceAll('[\\s\\-]', '');
                    if (normalizedFilename.contains('preapproval')) {
                        System.debug('Found pre-approval document - filename contains pre-approval variation: ' + doc.filename);
                        isPreApprovalDocument = true;
                    }
                }
                
                // Check if description, safeDescription, or summary contain "preapproval" or "pre-approval letter"
                if (!isPreApprovalDocument) {
                    if (doc.description != null) {
                        String normalizedDesc = doc.description.toLowerCase().replaceAll('[\\s\\-]', '');
                        if (normalizedDesc.contains('preapproval')) {
                            System.debug('Found pre-approval document - description contains pre-approval variation');
                            isPreApprovalDocument = true;
                        }
                    }
                    
                    if (!isPreApprovalDocument && doc.safeDescription != null) {
                        String normalizedSafeDesc = doc.safeDescription.toLowerCase().replaceAll('[\\s\\-]', '');
                        if (normalizedSafeDesc.contains('preapproval')) {
                            System.debug('Found pre-approval document - safeDescription contains pre-approval variation');
                            isPreApprovalDocument = true;
                        }
                    }
                    
                    if (!isPreApprovalDocument && doc.summary != null) {
                        String normalizedSummary = doc.summary.toLowerCase().replaceAll('[\\s\\-]', '');
                        if (normalizedSummary.contains('preapproval')) {
                            System.debug('Found pre-approval document - summary contains pre-approval variation');
                            isPreApprovalDocument = true;
                        }
                    }
                }
                
                if (isPreApprovalDocument) {
                    System.debug('Pre-approval document exists: ' + doc.filename);
                    return true;
                }
            }
            
            System.debug('No pre-approval document found in ' + documents.size() + ' documents');
            return false;
            
        } catch (Exception e) {
            System.debug('Error checking for pre-approval document existence: ' + e.getMessage());
            return false;
        }
    }

    // Helper method to extract pre-approval date from documents
    private static Date extractPreApprovalDateFromDocuments(List<Document> documents, Date applicationDate, List<CreditReport> creditReports) {
        try {
            System.debug('=== EXTRACTING PRE-APPROVAL DATE FROM DOCUMENTS ===');
            System.debug('Application date for validation: ' + applicationDate);
            
            if (documents == null || documents.isEmpty()) {
                System.debug('No documents provided');
                return null;
            }
            
            System.debug('Processing ' + documents.size() + ' documents');
            
            List<Date> preApprovalDates = new List<Date>();
            
            for (Document doc : documents) {
                System.debug('Checking document: ' + doc.filename);
                
                boolean isPreApprovalDocument = false;
                
                // Check if filename contains "preapproval" (normalized to handle variations like "Pre-Approval", "Pre Approval", etc.)
                if (doc.filename != null) {
                    String normalizedFilename = doc.filename.toLowerCase().replaceAll('[\\s\\-]', '');
                    if (normalizedFilename.contains('preapproval')) {
                        System.debug('Document filename contains pre-approval variation: ' + doc.filename);
                        isPreApprovalDocument = true;
                    }
                }
                
                // Check if description, safeDescription, or summary contain "preapproval"
                if (!isPreApprovalDocument) {
                    if (doc.description != null) {
                        String normalizedDesc = doc.description.toLowerCase().replaceAll('[\\s\\-]', '');
                        if (normalizedDesc.contains('preapproval')) {
                            System.debug('Document description contains pre-approval variation');
                            isPreApprovalDocument = true;
                        }
                    }
                    
                    if (!isPreApprovalDocument && doc.safeDescription != null) {
                        String normalizedSafeDesc = doc.safeDescription.toLowerCase().replaceAll('[\\s\\-]', '');
                        if (normalizedSafeDesc.contains('preapproval')) {
                            System.debug('Document safeDescription contains pre-approval variation');
                            isPreApprovalDocument = true;
                        }
                    }
                    
                    if (!isPreApprovalDocument && doc.summary != null) {
                        String normalizedSummary = doc.summary.toLowerCase().replaceAll('[\\s\\-]', '');
                        if (normalizedSummary.contains('preapproval')) {
                            System.debug('Document summary contains pre-approval variation');
                            isPreApprovalDocument = true;
                        }
                    }
                }
                
                if (isPreApprovalDocument) {
                    System.debug('Found pre-approval document: ' + doc.filename);
                    
                    // Step 1: Check if we have credit reports on file
                    Boolean hasCreditReports = (creditReports != null && !creditReports.isEmpty());
                    System.debug('Step 1 - Has credit reports: ' + hasCreditReports);
                    
                    // Step 2: Check if we have a date in description or safeDescription
                    Boolean hasDescriptionDate = hasDateInDescriptionOrSafeDescription(doc.description, doc.safeDescription);
                    System.debug('Step 2 - Has description/safeDescription date: ' + hasDescriptionDate);
                    
                    Date documentPreApprovalDate = null;
                    
                    // Step 3: Determine document pre-approval date based on conditions
                    if (hasCreditReports) {
                        // If 1 is TRUE, then our document pre-approval date is the date in filename
                        System.debug('Step 3 - Credit reports found, using filename date');
                        documentPreApprovalDate = extractDateFromFilename(doc.filename);
                        if (documentPreApprovalDate != null) {
                            System.debug('Extracted date from filename: ' + documentPreApprovalDate);
                        }
                    } else if (hasDescriptionDate) {
                        // If 1 is FALSE and 2 is TRUE, then our document pre-approval date is 30 days minus the date in description/safeDescription
                        System.debug('Step 3 - No credit reports but description date found, using description date minus 30 days');
                        documentPreApprovalDate = extractPreApprovalDateFromExpirationDate(doc.description, doc.safeDescription, doc.summary, doc.filename, creditReports);
                        if (documentPreApprovalDate != null) {
                            System.debug('Extracted pre-approval date from description: ' + documentPreApprovalDate);
                        }
                    } else {
                        // If 1 and 2 are both FALSE, then our document pre-approval date is the date in filename
                        System.debug('Step 3 - No credit reports and no description date, using filename date as fallback');
                        documentPreApprovalDate = extractDateFromFilename(doc.filename);
                        if (documentPreApprovalDate != null) {
                            System.debug('Extracted date from filename (fallback): ' + documentPreApprovalDate);
                        }
                    }
                    
                    // Step 4: Compare document date to application date
                    if (documentPreApprovalDate != null) {
                        System.debug('Step 4 - Document pre-approval date: ' + documentPreApprovalDate + ', Application date: ' + applicationDate);
                        if (applicationDate == null || documentPreApprovalDate >= applicationDate) {
                            // If the date is on or after the application submitted keyDate, use the document date
                            System.debug('Document date is on or after application date, using document date: ' + documentPreApprovalDate);
                            preApprovalDates.add(documentPreApprovalDate);
                        } else {
                            // If the date is before the application submitted keyDate, skip this document
                            // The pre-approval keyDate will be used as fallback in the main method
                            System.debug('Document date (' + documentPreApprovalDate + ') is before application date (' + applicationDate + '), skipping document - will use pre-approval keyDate fallback');
                        }
                    } else {
                        System.debug('Could not extract any date from document: ' + doc.filename);
                    }
                }
            }
            
            if (preApprovalDates.isEmpty()) {
                System.debug('No valid pre-approval dates found in documents (after application date validation)');
                return null;
            }
            
            // Sort dates to find the earliest (chronologically first)
            preApprovalDates.sort();
            Date earliestDate = preApprovalDates[0];
            
            System.debug('Found ' + preApprovalDates.size() + ' valid pre-approval dates, using earliest: ' + earliestDate);
            return earliestDate;
            
        } catch (Exception e) {
            System.debug('Error extracting pre-approval date from documents: ' + e.getMessage());
            return null;
        }
    }
    
    // Helper method to extract date from filename in format: filename_yyyymmdd.extension
    private static Date extractDateFromFilename(String filename) {
        try {
            if (filename == null || filename == '') {
                return null;
            }
            
            System.debug('Extracting date from filename: ' + filename);
            
            // Find the last underscore in the filename
            Integer lastUnderscoreIndex = filename.lastIndexOf('_');
            if (lastUnderscoreIndex == -1) {
                System.debug('No underscore found in filename');
                return null;
            }
            
            // Extract the part after the underscore
            String afterUnderscore = filename.substring(lastUnderscoreIndex + 1);
            System.debug('Part after underscore: ' + afterUnderscore);
            
            // Remove file extension if present (find the last dot)
            Integer lastDotIndex = afterUnderscore.lastIndexOf('.');
            String dateString = lastDotIndex != -1 ? afterUnderscore.substring(0, lastDotIndex) : afterUnderscore;
            
            System.debug('Date string extracted: ' + dateString);
            
            // Validate the date string format (should be 8 digits: yyyymmdd)
            if (dateString.length() != 8 || !dateString.isNumeric()) {
                System.debug('Date string is not in valid yyyymmdd format');
                return null;
            }
            
            // Parse the date
            String yearStr = dateString.substring(0, 4);
            String monthStr = dateString.substring(4, 6);
            String dayStr = dateString.substring(6, 8);
            
            Integer year = Integer.valueOf(yearStr);
            Integer month = Integer.valueOf(monthStr);
            Integer day = Integer.valueOf(dayStr);
            
            System.debug('Parsed date components - Year: ' + year + ', Month: ' + month + ', Day: ' + day);
            
            // Create and validate the date
            Date extractedDate = Date.newInstance(year, month, day);
            System.debug('Created date: ' + extractedDate);
            
            return extractedDate;
            
        } catch (Exception e) {
            System.debug('Error parsing date from filename: ' + e.getMessage());
            return null;
        }
    }

    // Helper method to check if borrower is Jane Doe
    private static Boolean isJaneDoe(Borrower borrower) {
        if (borrower == null) {
            return false;
        }
        
        String firstName = borrower.firstName;
        String lastName = borrower.lastName;
        
        // Check if both first and last name are "Jane" and "Doe" (case-insensitive)
        if (firstName != null && lastName != null) {
            return firstName.equalsIgnoreCase('Jane') && lastName.equalsIgnoreCase('Doe');
        }
        
        return false;
    }

    // Helper method to check if description or safeDescription contains a date
    private static Boolean hasDateInDescriptionOrSafeDescription(String description, String safeDescription) {
        try {
            // Check description field
            if (description != null && description != '') {
                // Look for date patterns like MM/DD/YYYY or MM-DD-YYYY or YYYY-MM-DD
                Pattern datePattern = Pattern.compile('(\\d{1,2}[/-]\\d{1,2}[/-]\\d{4}|\\d{4}[/-]\\d{1,2}[/-]\\d{1,2})');
                Matcher dateMatcher = datePattern.matcher(description);
                if (dateMatcher.find()) {
                    System.debug('Found date in description: ' + dateMatcher.group());
                    return true;
                }
            }
            
            // Check safeDescription field
            if (safeDescription != null && safeDescription != '') {
                // Look for date patterns like MM/DD/YYYY or MM-DD-YYYY or YYYY-MM-DD
                Pattern datePattern = Pattern.compile('(\\d{1,2}[/-]\\d{1,2}[/-]\\d{4}|\\d{4}[/-]\\d{1,2}[/-]\\d{1,2})');
                Matcher dateMatcher = datePattern.matcher(safeDescription);
                if (dateMatcher.find()) {
                    System.debug('Found date in safeDescription: ' + dateMatcher.group());
                    return true;
                }
            }
            
            System.debug('No date found in description or safeDescription');
            return false;
            
        } catch (Exception e) {
            System.debug('Error checking for date in description/safeDescription: ' + e.getMessage());
            return false;
        }
    }

    // Helper method to extract pre-approval date from expiration date in any document field (subtract 30 or 90 days based on creditReportRef)
    private static Date extractPreApprovalDateFromExpirationDate(String description, String safeDescription, String summary, String filename, List<CreditReport> creditReports) {
        try {
            System.debug('=== EXTRACTING PRE-APPROVAL DATE FROM EXPIRATION DATE ===');

            // Check each field for expiration patterns
            List<String> fieldsToCheck = new List<String>();
            if (description != null && description != '') fieldsToCheck.add('description:' + description);
            if (safeDescription != null && safeDescription != '') fieldsToCheck.add('safeDescription:' + safeDescription);
            if (summary != null && summary != '') fieldsToCheck.add('summary:' + summary);
            if (filename != null && filename != '') fieldsToCheck.add('filename:' + filename);

            if (fieldsToCheck.isEmpty()) {
                System.debug('No document fields provided');
                return null;
            }

            // Look for expiration patterns in each field
            String expirationPattern1 = 'expiration date:';
            String expirationPattern2 = 'expires';
            String expirationPattern3 = 'expiration:';
            String preApprovalPattern = 'pre-approval letter,';
            String expPattern1 = 'exp ';
            String expPattern2 = 'expire';
            String expPattern3 = 'expired';

            Integer bestPatternIndex = -1;
            String bestPattern = '';
            String bestSourceText = '';
            String bestFieldName = '';

            for (String fieldWithLabel : fieldsToCheck) {
                // Extract field name and content
                Integer colonIndex = fieldWithLabel.indexOf(':');
                String fieldName = fieldWithLabel.substring(0, colonIndex);
                String fieldContent = fieldWithLabel.substring(colonIndex + 1);

                System.debug('Checking ' + fieldName + ' for expiration patterns');

                String contentLower = fieldContent.toLowerCase();

                // Check for "expiration date:" pattern
                Integer patternIndex1 = contentLower.indexOf(expirationPattern1.toLowerCase());
                if (patternIndex1 != -1 && (bestPatternIndex == -1 || patternIndex1 < bestPatternIndex)) {
                    bestPatternIndex = patternIndex1;
                    bestPattern = expirationPattern1;
                    bestSourceText = fieldContent;
                    bestFieldName = fieldName;
                }

                // Check for "expires" pattern
                Integer patternIndex2 = contentLower.indexOf(expirationPattern2.toLowerCase());
                if (patternIndex2 != -1 && (bestPatternIndex == -1 || patternIndex2 < bestPatternIndex)) {
                    bestPatternIndex = patternIndex2;
                    bestPattern = expirationPattern2;
                    bestSourceText = fieldContent;
                    bestFieldName = fieldName;
                }

                // Check for "expiration:" pattern
                Integer patternIndex3 = contentLower.indexOf(expirationPattern3.toLowerCase());
                if (patternIndex3 != -1 && (bestPatternIndex == -1 || patternIndex3 < bestPatternIndex)) {
                    bestPatternIndex = patternIndex3;
                    bestPattern = expirationPattern3;
                    bestSourceText = fieldContent;
                    bestFieldName = fieldName;
                }

                // Check for "pre-approval letter," pattern
                Integer preApprovalIndex = contentLower.indexOf(preApprovalPattern.toLowerCase());
                if (preApprovalIndex != -1 && (bestPatternIndex == -1 || preApprovalIndex < bestPatternIndex)) {
                    bestPatternIndex = preApprovalIndex;
                    bestPattern = preApprovalPattern;
                    bestSourceText = fieldContent;
                    bestFieldName = fieldName;
                }

                // Check for "exp " pattern
                Integer expIndex1 = contentLower.indexOf(expPattern1);
                if (expIndex1 != -1 && (bestPatternIndex == -1 || expIndex1 < bestPatternIndex)) {
                    bestPatternIndex = expIndex1;
                    bestPattern = expPattern1;
                    bestSourceText = fieldContent;
                    bestFieldName = fieldName;
                }

                // Check for "expire" pattern
                Integer expIndex2 = contentLower.indexOf(expPattern2);
                if (expIndex2 != -1 && (bestPatternIndex == -1 || expIndex2 < bestPatternIndex)) {
                    bestPatternIndex = expIndex2;
                    bestPattern = expPattern2;
                    bestSourceText = fieldContent;
                    bestFieldName = fieldName;
                }

                // Check for "expired" pattern
                Integer expIndex3 = contentLower.indexOf(expPattern3);
                if (expIndex3 != -1 && (bestPatternIndex == -1 || expIndex3 < bestPatternIndex)) {
                    bestPatternIndex = expIndex3;
                    bestPattern = expPattern3;
                    bestSourceText = fieldContent;
                    bestFieldName = fieldName;
                }
            }

            if (bestPatternIndex == -1) {
                System.debug('No expiration patterns found in any document field');
                return null;
            }

            System.debug('Found "' + bestPattern + '" pattern at index: ' + bestPatternIndex + ' in ' + bestFieldName);

            // Extract text after the pattern
            String afterPattern = bestSourceText.substring(bestPatternIndex + bestPattern.length()).trim();
            System.debug('Text after "' + bestPattern + '": ' + afterPattern);

            // Find the date immediately following the pattern
            // Look for date patterns like MM/DD/YYYY or MM-DD-YYYY or YYYY-MM-DD
            Pattern datePattern = Pattern.compile('(\\d{1,2}[/-]\\d{1,2}[/-]\\d{4}|\\d{4}[/-]\\d{1,2}[/-]\\d{1,2})');
            Matcher dateMatcher = datePattern.matcher(afterPattern);

            if (!dateMatcher.find()) {
                System.debug('No valid date pattern found after "' + bestPattern + '"');
                return null;
            }

            String dateString = dateMatcher.group();
            System.debug('Found date string: ' + dateString);

            // Parse the date string
            Date expirationDate = parseDateString(dateString);
            if (expirationDate == null) {
                System.debug('Could not parse date string: ' + dateString);
                return null;
            }

            System.debug('Parsed expiration date: ' + expirationDate);

            // When this method is called, it means we have no credit reports, so always subtract 30 days
            Integer daysToSubtract = 30;
            System.debug('No credit reports found - using 30 days for pre-approval calculation');

            // Subtract the determined number of days to get the pre-approval date
            Date preApprovalDate = expirationDate.addDays(-daysToSubtract);
            System.debug('Pre-approval date (expiration date - ' + daysToSubtract + ' days): ' + preApprovalDate);

            return preApprovalDate;

        } catch (Exception e) {
            System.debug('Error extracting pre-approval date from expiration date: ' + e.getMessage());
            return null;
        }
    }
    
    // Helper method to parse various date string formats
    private static Date parseDateString(String dateString) {
        try {
            if (dateString == null || dateString == '') {
                return null;
            }
            
            System.debug('Parsing date string: ' + dateString);
            
            // Remove any extra whitespace
            dateString = dateString.trim();
            
            // Handle different date formats
            String[] dateParts;
            
            if (dateString.contains('/')) {
                dateParts = dateString.split('/');
            } else if (dateString.contains('-')) {
                dateParts = dateString.split('-');
            } else {
                System.debug('Unsupported date format: ' + dateString);
                return null;
            }
            
            if (dateParts.size() != 3) {
                System.debug('Invalid date format - expected 3 parts, got: ' + dateParts.size());
                return null;
            }
            
            Integer year, month, day;
            
            // Determine if it's MM/DD/YYYY or YYYY/MM/DD format
            if (dateParts[0].length() == 4) {
                // YYYY/MM/DD format
                year = Integer.valueOf(dateParts[0]);
                month = Integer.valueOf(dateParts[1]);
                day = Integer.valueOf(dateParts[2]);
            } else {
                // MM/DD/YYYY format
                month = Integer.valueOf(dateParts[0]);
                day = Integer.valueOf(dateParts[1]);
                year = Integer.valueOf(dateParts[2]);
            }
            
            System.debug('Parsed date components - Year: ' + year + ', Month: ' + month + ', Day: ' + day);
            
            // Create and validate the date
            Date parsedDate = Date.newInstance(year, month, day);
            System.debug('Created date: ' + parsedDate);
            
            return parsedDate;
            
        } catch (Exception e) {
            System.debug('Error parsing date string: ' + e.getMessage() + ' - Input: ' + dateString);
            return null;
        }
    }

    // Helper method to convert ISO date string to Salesforce Date
    private static Date convertISOStringToDate(String isoDateString) {
        try {
            if (isoDateString == null || isoDateString == '') {
                return null;
            }
            
            // Handle ISO format: "2025-06-13T00:00:00Z"
            String dateOnly = isoDateString.split('T')[0];
            return Date.valueOf(dateOnly);
        } catch (Exception e) {
            System.debug('Error converting ISO date string to Date: ' + e.getMessage() + ' - Input: ' + isoDateString);
            return null;
        }
    }

    private static String orgTimeZoneSidCache;

    /** Organization default time zone (Company Information), cached for flat-fee TRID date comparison. */
    private static String getOrganizationTimeZoneSid() {
        if (orgTimeZoneSidCache == null) {
            orgTimeZoneSidCache = [SELECT TimeZoneSidKey FROM Organization LIMIT 1].TimeZoneSidKey;
        }
        return orgTimeZoneSidCache;
    }

    /**
     * Parse Sonar-style ISO timestamps to a GMT Datetime for calendar-date conversion.
     * Zulu (Z) values are interpreted as UTC; if parsing fails, returns null.
     */
    private static Datetime parseTridIsoToUtcDatetime(String isoDateString) {
        if (isoDateString == null || isoDateString.trim() == '') {
            return null;
        }
        String s = isoDateString.trim();
        try {
            Boolean endsZ = s.endsWithIgnoreCase('Z');
            if (endsZ) {
                s = s.substring(0, s.length() - 1);
            }
            List<String> dtParts = s.split('T', 2);
            List<String> ymd = dtParts[0].split('-');
            if (ymd.size() != 3) {
                return null;
            }
            Integer y = Integer.valueOf(ymd[0]);
            Integer mo = Integer.valueOf(ymd[1]);
            Integer d = Integer.valueOf(ymd[2]);
            Integer hh = 0;
            Integer mm = 0;
            Integer ss = 0;
            if (dtParts.size() > 1) {
                String tp = dtParts[1];
                Integer dot = tp.indexOf('.');
                if (dot > 0) {
                    tp = tp.substring(0, dot);
                }
                Integer plusIdx = tp.indexOf('+');
                if (plusIdx > 0) {
                    tp = tp.substring(0, plusIdx);
                } else {
                    Integer minusIdx = tp.lastIndexOf('-');
                    if (minusIdx > 2) {
                        tp = tp.substring(0, minusIdx);
                    }
                }
                List<String> hms = tp.split(':');
                if (hms.size() >= 1 && hms[0] != null && hms[0] != '') {
                    hh = Integer.valueOf(hms[0]);
                }
                if (hms.size() >= 2 && hms[1] != null && hms[1] != '') {
                    mm = Integer.valueOf(hms[1]);
                }
                if (hms.size() >= 3 && hms[2] != null && hms[2] != '') {
                    ss = Integer.valueOf(hms[2]);
                }
            }
            return Datetime.newInstanceGmt(y, mo, d, hh, mm, ss);
        } catch (Exception e) {
            System.debug('parseTridIsoToUtcDatetime failed: ' + e.getMessage() + ' input=' + isoDateString);
            return null;
        }
    }

    /**
     * TRID application date as a calendar date in the org default time zone (for flat-fee tiering).
     * Falls back to convertISOStringToDate if time parsing fails.
     */
    private static Date tridIsoToOrgLocalDate(String isoDateString) {
        Datetime utcDt = parseTridIsoToUtcDatetime(isoDateString);
        if (utcDt == null) {
            return convertISOStringToDate(isoDateString);
        }
        try {
            String localYmd = utcDt.format('yyyy-MM-dd', getOrganizationTimeZoneSid());
            return Date.valueOf(localYmd);
        } catch (Exception e) {
            System.debug('tridIsoToOrgLocalDate fallback: ' + e.getMessage());
            return convertISOStringToDate(isoDateString);
        }
    }

    /**
     * Resolves TRID/application date for flat-fee rules: TridDate from this payload (org-local calendar date)
     * when present, otherwise Application_Date__c on the RLA (e.g. loaded from a prior save).
     */
    private static Date resolveTridApplicationDateForFlatFee(loanApplicationWrapper app, ResidentialLoanApplication__c rla) {
        if (app.keyDates != null) {
            for (KeyDate kd : app.keyDates) {
                if (kd != null && kd.name == 'TridDate' && kd.value != null && kd.value != '') {
                    return tridIsoToOrgLocalDate(kd.value);
                }
            }
        }
        return rla.Application_Date__c;
    }

    /**
     * Flat fee to subtract from first BrokerFee when computing Broker_Compensation_Percentage__c (Lender Paid only).
     * Returns null when no lender-specific flat fee applies (use gross broker fee / loan amount).
     */
    @TestVisible
    private static Decimal getLenderPaidFlatFeeDeduction(String translatedLenderName, String transactionType, Date tridAppDateOrgLocal) {
        final Date kindWindsorCutoff = Date.newInstance(2026, 3, 31);
        final Date tlsCutoff = Date.newInstance(2026, 4, 2);

        if (translatedLenderName == null) {
            return null;
        }

        if (translatedLenderName == 'Kind Lending') {
            if (tridAppDateOrgLocal == null) {
                return 750;
            }
            return tridAppDateOrgLocal < kindWindsorCutoff ? 150 : 750;
        }
        if (translatedLenderName == 'Windsor Mortgage') {
            if (tridAppDateOrgLocal == null) {
                return 1000;
            }
            if (tridAppDateOrgLocal < kindWindsorCutoff) {
                return null;
            }
            return 1000;
        }
        if (translatedLenderName == 'The Loan Store') {
            if (tridAppDateOrgLocal == null) {
                return 750;
            }
            if (tridAppDateOrgLocal < tlsCutoff) {
                return null;
            }
            return 750;
        }
        if (translatedLenderName == 'Rocket Pro TPO' && transactionType != null && transactionType.contains('Purchase')) {
            return 150;
        }
        if (translatedLenderName == 'Plaza Home Mortgage') {
            return 150;
        }
        return null;
    }

    // Map state/province abbreviation to full name for MailingState picklist (FIELD_INTEGRITY_EXCEPTION if abbreviation used)
    @TestVisible
    private static String stateAbbreviationToFullName(String stateOrAbbrev) {
        if (stateOrAbbrev == null || stateOrAbbrev.trim() == '') return stateOrAbbrev;
        String key = stateOrAbbrev.trim().toUpperCase();
        if (key.length() != 2) return stateOrAbbrev.trim(); // assume already full name
        Map<String, String> abbrToFull = new Map<String, String>{
            'AL'=>'Alabama','AK'=>'Alaska','AZ'=>'Arizona','AR'=>'Arkansas','CA'=>'California','CO'=>'Colorado',
            'CT'=>'Connecticut','DE'=>'Delaware','DC'=>'District of Columbia','FL'=>'Florida','GA'=>'Georgia',
            'HI'=>'Hawaii','ID'=>'Idaho','IL'=>'Illinois','IN'=>'Indiana','IA'=>'Iowa','KS'=>'Kansas',
            'KY'=>'Kentucky','LA'=>'Louisiana','ME'=>'Maine','MD'=>'Maryland','MA'=>'Massachusetts','MI'=>'Michigan',
            'MN'=>'Minnesota','MS'=>'Mississippi','MO'=>'Missouri','MT'=>'Montana','NE'=>'Nebraska','NV'=>'Nevada',
            'NH'=>'New Hampshire','NJ'=>'New Jersey','NM'=>'New Mexico','NY'=>'New York','NC'=>'North Carolina',
            'ND'=>'North Dakota','OH'=>'Ohio','OK'=>'Oklahoma','OR'=>'Oregon','PA'=>'Pennsylvania','RI'=>'Rhode Island',
            'SC'=>'South Carolina','SD'=>'South Dakota','TN'=>'Tennessee','TX'=>'Texas','UT'=>'Utah','VT'=>'Vermont',
            'VA'=>'Virginia','WA'=>'Washington','WV'=>'West Virginia','WI'=>'Wisconsin','WY'=>'Wyoming',
            'AB'=>'Alberta','BC'=>'British Columbia','MB'=>'Manitoba','NB'=>'New Brunswick','NL'=>'Newfoundland and Labrador',
            'NS'=>'Nova Scotia','NT'=>'Northwest Territories','NU'=>'Nunavut','ON'=>'Ontario','PE'=>'Prince Edward Island',
            'QC'=>'Quebec','SK'=>'Saskatchewan','YT'=>'Yukon'
        };
        return abbrToFull.containsKey(key) ? abbrToFull.get(key) : stateOrAbbrev.trim();
    }

    // Truncate string to max length for Salesforce field limits (prevents DML errors)
    @TestVisible
    private static String truncateToLength(String s, Integer maxLen) {
        if (s == null || maxLen == null || maxLen <= 0) return s;
        if (s.length() <= maxLen) return s;
        return s.substring(0, maxLen);
    }

    // Helper method to format fee type name from CamelCase to Title Case
    private static String formatFeeTypeName(String feeType) {
        if (feeType == null || feeType == '') {
            return feeType;
        }
        
        try {
            // Use regex to insert spaces before uppercase letters that follow lowercase letters or numbers
            String spacedString = feeType.replaceAll('([a-z0-9])([A-Z])', '$1 $2');
            
            // Split the string into words
            List<String> words = spacedString.split(' ');
            List<String> formattedWords = new List<String>();
            
            for (String word : words) {
                if (word.length() > 0) {
                    // Convert specific words to lowercase (articles, prepositions, conjunctions)
                    if (word.toLowerCase() == 'for' || word.toLowerCase() == 'of' || 
                        word.toLowerCase() == 'and' || word.toLowerCase() == 'the' || 
                        word.toLowerCase() == 'in' || word.toLowerCase() == 'on' || 
                        word.toLowerCase() == 'at' || word.toLowerCase() == 'to') {
                        // Keep these words lowercase unless they're the first word
                        if (formattedWords.isEmpty()) {
                            formattedWords.add(word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase());
                        } else {
                            formattedWords.add(word.toLowerCase());
                        }
                    } else {
                        // Capitalize first letter, lowercase the rest
                        formattedWords.add(word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase());
                    }
                }
            }
            
            String result = String.join(formattedWords, ' ');
            
            // Apply specific fee type corrections
            
            // Fix spelling: Preperation -> Preparation
            result = result.replaceAll('(?i)Preperation', 'Preparation');
            
            // Fix acronyms: Cpl -> CPL, Fha -> FHA, Mers -> MERS, Va -> VA
            // Use word boundaries to ensure we only replace whole words
            result = result.replaceAll('(?i)\\bCpl\\b', 'CPL');
            result = result.replaceAll('(?i)\\bFha\\b', 'FHA');
            result = result.replaceAll('(?i)\\bMers\\b', 'MERS');
            result = result.replaceAll('(?i)\\bVa\\b', 'VA');
            
            // Fix hyphenation: Tiein -> Tie-In
            result = result.replaceAll('(?i)Tiein', 'Tie-In');
            
            // Fix specific phrases
            if (result.equalsIgnoreCase('Services You Cannot Shop for Other')) {
                result = 'Services You Cannot Shop For - Other';
            }
            if (result.equalsIgnoreCase('Services You Can Shop for Other')) {
                result = 'Services You Can Shop For - Other';
            }
            
            System.debug('Formatted fee type: "' + feeType + '" -> "' + result + '"');
            return result;
        } catch (Exception e) {
            System.debug('Error formatting fee type name: ' + e.getMessage() + ' - Input: ' + feeType);
            return feeType; // Return original if formatting fails
        }
    }

    // METHOD NAME: getProductData
    // DESCRIPTION: Method to retrieve product data from Sonar using API key authentication
    // Parameters ------------------------------------------------------------------------------------------
    // loanId : Sonar Loan ID to retrieve product data for
    private static String getProductData(String loanId) {
        try {
            System.debug('=== STARTING PRODUCT DATA API CALL ===');
            System.debug('Loan ID for product data: ' + loanId);
            
            // Setup Custom Setting Object
            Sonar_API__c apiSettings = Sonar_API__c.getInstance();
            System.debug('API Settings - WebServices_URL__c: ' + (apiSettings != null ? apiSettings.WebServices_URL__c : 'null'));
            System.debug('API Settings - Client_Secret__c: ' + (apiSettings != null ? '***' + apiSettings.Client_Secret__c.right(4) : 'null'));
            
            // Create the HTTP request
            Http http = new Http();
            HttpRequest request = new HttpRequest();
            
            // Set the Endpoint URL for product data
            String endPointAPI = apiSettings.WebServices_URL__c + '/api/v1/loans/' + loanId + '/selected-mortgage-product';
            request.setEndpoint(endPointAPI);
            
            // Set the authorization header with API key
            request.setHeader('x-api-key', apiSettings.Client_Secret__c);
            request.setHeader('Content-Type', 'application/json');
            request.setMethod('GET');
            
            // Set timeout
            request.setTimeout(60000); // 1 minute timeout
            
            System.debug('Making request to Sonar Product API: ' + endPointAPI);
            System.debug('Request method: ' + request.getMethod());
            System.debug('Request headers: ' + request.getHeader('x-api-key') + ', ' + request.getHeader('Content-Type'));
            
            // Send the request
            HttpResponse response = http.send(request);
            System.debug('Product API Response Status: ' + response.getStatus());
            System.debug('Product API Response Status Code: ' + response.getStatusCode());
            System.debug('Product API Response Body Length: ' + (response.getBody() != null ? String.valueOf(response.getBody().length()) : 'null'));
            System.debug('Product API Response Body: ' + response.getBody());
            
            // Check if the request is successful
            if (response.getStatusCode() == 200) {
                System.debug('Product Data received from Sonar successfully');
                String responseBody = response.getBody();
                System.debug('Product API Response Body Length: ' + String.valueOf(responseBody.length()));
                System.debug('Product API Response Body: ' + responseBody);
                System.debug('Returning product data with length: ' + String.valueOf(responseBody.length()));
                return responseBody;
            } else {
                System.debug('Product API Error Status: ' + response.getStatus());
                System.debug('Product API Error Status Code: ' + response.getStatusCode());
                System.debug('Product API Error Body: ' + response.getBody());
                
                // Return empty JSON object if product data is not available
                System.debug('Product data not available, returning empty object');
                return '{}';
            }
            
        } catch (Exception e) {
            System.debug('Error getting product data from Sonar: ' + e.getMessage());
            System.debug('Error stack trace: ' + e.getStackTraceString());
            // Return empty JSON object if there's an error
            return '{}';
        }
    }

    // Parse lienPosition from GET /loans/{id}/transaction response (root or nested under "transaction").
    @TestVisible
    private static Integer getLienPositionFromTransactionJson(String transactionDataJson) {
        if (transactionDataJson == null || transactionDataJson.trim() == '' || transactionDataJson.trim() == '{}') return null;
        try {
            Map<String, Object> root = (Map<String, Object>)JSON.deserializeUntyped(transactionDataJson);
            if (root == null) return null;
            Object val = root.get('lienPosition');
            if (val == null && root.containsKey('transaction')) {
                Object trans = root.get('transaction');
                if (trans instanceof Map<String, Object>) {
                    val = ((Map<String, Object>)trans).get('lienPosition');
                }
            }
            if (val instanceof Integer) return (Integer)val;
            if (val instanceof Decimal) return ((Decimal)val).intValue();
            if (val instanceof String) {
                String s = (String)val;
                if (s.trim() == '') return null;
                return Integer.valueOf(s.trim());
            }
            return null;
        } catch (Exception e) {
            System.debug('Error parsing lienPosition from transaction JSON: ' + e.getMessage());
            return null;
        }
    }

    // METHOD NAME: getTransactionData
    // DESCRIPTION: GET /api/v1/loans/{loanId}/transaction to retrieve transaction data (e.g. lien position).
    private static String getTransactionData(String loanId) {
        try {
            Sonar_API__c apiSettings = Sonar_API__c.getInstance();
            if (apiSettings == null || apiSettings.WebServices_URL__c == null) return '{}';
            Http http = new Http();
            HttpRequest request = new HttpRequest();
            String endPointAPI = apiSettings.WebServices_URL__c + '/api/v1/loans/' + loanId + '/transaction';
            request.setEndpoint(endPointAPI);
            request.setHeader('x-api-key', apiSettings.Client_Secret__c);
            request.setHeader('Content-Type', 'application/json');
            request.setMethod('GET');
            request.setTimeout(60000);
            System.debug('Making request to Sonar Transaction API: ' + endPointAPI);
            HttpResponse response = http.send(request);
            if (response.getStatusCode() == 200) {
                String body = response.getBody();
                System.debug('Transaction data received from Sonar, length: ' + (body != null ? body.length() : 0));
                return body != null ? body : '{}';
            }
            System.debug('Transaction API status: ' + response.getStatusCode() + ', body: ' + response.getBody());
            return '{}';
        } catch (Exception e) {
            System.debug('Error getting transaction data from Sonar: ' + e.getMessage());
            return '{}';
        }
    }

    public class LastModifiedDateComparator implements System.Comparator<Fee__c> {
        public Integer compare(Fee__c f1, Fee__c f2) {
            if (f1.LastModifiedDate == null && f2.LastModifiedDate == null) return 0;
            if (f1.LastModifiedDate == null) return 1;
            if (f2.LastModifiedDate == null) return -1;
            // Sort in descending order (most recent first)
            return f2.LastModifiedDate > f1.LastModifiedDate ? 1 : (f2.LastModifiedDate < f1.LastModifiedDate ? -1 : 0);
        }
    }

    /**
     * Resolves processor email value when Sonar has a processor team member
     * This method is only called when we've already confirmed Sonar has a processor team member
     * 
     * @param salesforceProcessorEmail The current processor email value in Salesforce
     * @param sonarProcessorEmail The processor email value from Sonar webhook (could be null/blank if processor has no email)
     * @param sonarHasProcessor Confirmation that Sonar has a processor team member
     * @return The resolved processor email value (Sonar's value)
     */
    private static String resolveProcessorEmailConflict(String salesforceProcessorEmail, String sonarProcessorEmail, Boolean sonarHasProcessor) {
        System.debug('=== PROCESSOR EMAIL CONFLICT RESOLUTION ===');
        System.debug('Salesforce Processor Email: ' + salesforceProcessorEmail);
        System.debug('Sonar Processor Email: ' + sonarProcessorEmail);
        System.debug('Sonar has processor team member: ' + sonarHasProcessor);
        
        // Since this method is only called when Sonar has a processor team member,
        // we always use Sonar's processor email value (even if it's blank)
        String resolvedEmail = sonarProcessorEmail;
        String resolutionReason = 'Sonar has processor team member, using Sonar processor email: ' + sonarProcessorEmail;
        
        System.debug('Resolution decision: ' + resolutionReason);
        System.debug('Resolved processor email: ' + resolvedEmail);
        System.debug('=== END PROCESSOR EMAIL CONFLICT RESOLUTION ===');
        
        return resolvedEmail;
    }

    // Circuit breaker configuration (matching SonarTeamMemberUpdate pattern)
    private static final Integer CIRCUIT_BREAKER_FAILURE_THRESHOLD = 5;
    private static final Integer CIRCUIT_BREAKER_TIMEOUT_MINUTES = 10;
    
    // Circuit breaker state tracking (in-memory, no custom settings needed)
    private static Map<String, SonarCircuitBreakerState> circuitBreakerStates = new Map<String, SonarCircuitBreakerState>();
    
    /**
     * Circuit breaker state class (matching SonarTeamMemberUpdate pattern)
     */
    public class SonarCircuitBreakerState {
        public Integer failureCount = 0;
        public DateTime lastFailureTime;
        public Boolean isOpen = false;
        public String serviceName;
        
        public SonarCircuitBreakerState(String service) {
            this.serviceName = service;
        }
    }
    
    /**
     * Circuit breaker pattern implementation (matching SonarTeamMemberUpdate pattern)
     */
    private static Boolean isSonarCircuitOpen() {
        String serviceName = 'SonarAPI';
        SonarCircuitBreakerState state = circuitBreakerStates.get(serviceName);
        
        if (state == null) {
            state = new SonarCircuitBreakerState(serviceName);
            circuitBreakerStates.put(serviceName, state);
            return false;
        }
        
        if (state.isOpen) {
            // Check if timeout period has passed
            if (state.lastFailureTime != null && 
                DateTime.now().getTime() - state.lastFailureTime.getTime() > (CIRCUIT_BREAKER_TIMEOUT_MINUTES * 60 * 1000)) {
                System.debug('Circuit breaker timeout expired for ' + serviceName + ' - attempting to close');
                state.isOpen = false;
                state.failureCount = 0;
                return false;
            }
            return true;
        }
        
        return false;
    }
    
    /**
     * Record circuit breaker failure (matching SonarTeamMemberUpdate pattern)
     */
    private static void recordSonarFailure() {
        String serviceName = 'SonarAPI';
        SonarCircuitBreakerState state = circuitBreakerStates.get(serviceName);
        
        if (state == null) {
            state = new SonarCircuitBreakerState(serviceName);
            circuitBreakerStates.put(serviceName, state);
        }
        
        state.failureCount++;
        state.lastFailureTime = DateTime.now();
        
        if (state.failureCount >= CIRCUIT_BREAKER_FAILURE_THRESHOLD) {
            state.isOpen = true;
            System.debug('Circuit breaker OPENED for ' + serviceName + ' after ' + state.failureCount + ' failures');
        } else {
            System.debug('Circuit breaker failure recorded for ' + serviceName + ' (count: ' + state.failureCount + ')');
        }
    }
    
    /**
     * Record circuit breaker success (matching SonarTeamMemberUpdate pattern)
     */
    private static void recordSonarSuccess() {
        String serviceName = 'SonarAPI';
        SonarCircuitBreakerState state = circuitBreakerStates.get(serviceName);
        
        if (state != null) {
            state.failureCount = 0;
            if (state.isOpen) {
                state.isOpen = false;
                System.debug('Circuit breaker CLOSED for ' + serviceName + ' after successful call');
            }
        }
    }
    
    /**
     * Get circuit breaker status for monitoring (matching SonarTeamMemberUpdate pattern)
     */
    public static Map<String, Object> getSonarCircuitBreakerStatus() {
        Map<String, Object> status = new Map<String, Object>();
        String serviceName = 'SonarAPI';
        SonarCircuitBreakerState state = circuitBreakerStates.get(serviceName);
        
        if (state != null) {
            status.put('serviceName', serviceName);
            status.put('isOpen', state.isOpen);
            status.put('failureCount', state.failureCount);
            status.put('lastFailureTime', state.lastFailureTime);
        } else {
            status.put('serviceName', serviceName);
            status.put('isOpen', false);
            status.put('failureCount', 0);
            status.put('lastFailureTime', null);
        }
        
        return status;
    }
    
    /**
     * Reset circuit breaker for testing or manual intervention (matching SonarTeamMemberUpdate pattern)
     */
    public static void resetSonarCircuitBreaker() {
        String serviceName = 'SonarAPI';
        if (circuitBreakerStates.containsKey(serviceName)) {
            SonarCircuitBreakerState state = circuitBreakerStates.get(serviceName);
            state.failureCount = 0;
            state.lastFailureTime = null;
            state.isOpen = false;
            System.debug('Circuit breaker reset for ' + serviceName);
        }
    }
    
    /**
     * Check if Sonar is experiencing timeout issues based on recent failures
     */
    public static Boolean isSonarExperiencingTimeouts() {
        String serviceName = 'SonarAPI';
        SonarCircuitBreakerState state = circuitBreakerStates.get(serviceName);
        
        if (state != null && state.failureCount >= 2) {
            // If we have 2+ recent failures, Sonar might be experiencing issues
            return true;
        }
        
        return false;
    }
    
    /**
     * Get detailed Sonar API health status
     */
    public static Map<String, Object> getSonarHealthStatus() {
        Map<String, Object> health = new Map<String, Object>();
        Map<String, Object> circuitStatus = getSonarCircuitBreakerStatus();
        
        health.put('circuitBreaker', circuitStatus);
        health.put('isExperiencingTimeouts', isSonarExperiencingTimeouts());
        health.put('recommendation', getSonarRecommendation());
        
        return health;
    }
    
    /**
     * Get recommendation based on Sonar API status
     */
    private static String getSonarRecommendation() {
        Map<String, Object> status = getSonarCircuitBreakerStatus();
        Boolean isOpen = (Boolean)status.get('isOpen');
        Integer failureCount = (Integer)status.get('failureCount');
        
        if (isOpen) {
            return 'Sonar API is currently unavailable. Circuit breaker is open. Please wait 5 minutes before retrying.';
        } else if (failureCount >= 2) {
            return 'Sonar API is experiencing issues. Consider delaying non-critical operations.';
        } else {
            return 'Sonar API appears to be functioning normally.';
        }
    }

}