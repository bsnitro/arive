# Sample Payload Mapping Matrix

Source payload: `sampleAriveLoanJL.json`  
Target object: `ResidentialLoanApplication__c` (+ `Fee__c`, `Condition__c`)  
Handler: `services/salesforce-loan-application-sync-service.ts`

## Mapped in current implementation

- `ariveLoanId` / `loanId` -> `LOS_ID__c`
- `externalId` / `sysGUID` + `LOS_ID__c` -> `Sonar_GUID__c`
- `externalId` / `sysGUID` -> `ApplicationExtIdentifier__c`
- `lenderLoanIdentifier` / `loanNumber` -> `Loan_Number__c`
- `attributes[LenderLoanNumber]` -> `Loan_Number__c` (preferred when present)
- `attributes[TitleCompanyFileNumber]` -> `Title_Company_File_Number__c`
- `loanAmount` / `totalLoanAmount` -> `Total_Loan_Amount__c`
- `loanAmount` adjusted by FHA/VA fees -> `Loan_Amount__c`
- `currentLoanStatus.status` / `loanStatus` -> `Status__c`
- `currentLoanStatus.adverseReason` / `loanStatusReason` -> `LoanSubStatus__c`
- `applicationDate` / `createDateTime` -> `Date_File_Started__c`
- `applicationDate` / `createDateTime` / `keyDates.tridDate` -> `Application_Date__c`
- `ltv` / `ltvRatio` -> `Loan_to_Value__c`
- `cltv` / `cltvRatio` -> `Combined_Loan_to_Value__c`
- `backEndDTI` / `dtiRatio` -> `Top_End_Debt_to_Income__c`
- `loanPurpose` / `transactionType` -> `LoanPurpose__c` (mapped values)
- `mortgageType` / `loanType` / product mortgage type -> `Loan_Product__c`
- `lenderProductName` / product fields -> `Additional_Loan_Product_Details__c`
- `lenderName` / product lender name -> `Lender_Name__c`
- lender translation rules from Apex are applied (`Jmac Lending`, `Pennymac`, `Rocket Mortgage`, `Sierra Pacific`, `Undefined`)
- `lienPosition` / transaction `lienPosition` -> `Lien_Position_Number__c`, `Lien_Position__c`
- `lockStatus` -> `Lock_Status__c` (`None` -> `Not Locked`)
- `industryChannel` -> `Channelc__c` (`Broker` -> `Brokered`)
- `amortizationType` -> `loanAmortizationType__c`
- `subjectProperty.propertyUsageType` -> `Occupancy_Type__c`
- `wireAmountReceived` -> `Wire_Check_Amount_Received__c`
- `wireReceivedDate` -> `Wire_Check_Date_Received__c`
- `firstTimeHomeBuyer` (loan or primary borrower) -> `First_Time_Homebuyer__c`
- product/loan rate -> `InterestRate__c` (decimal converted to percent)
- product/loan term -> `Loan_Term_Months__c`
- principal/interest payment -> `Proposed_Payment_First_Mortgage__c`
- MI payment -> `Proposed_Payment_Mortgage_Insurance__c`
- `realEstateTaxMonthlyAmt` -> `Proposed_Payment_Property_Taxes__c`
- `homeownersInsuranceMonthlyAmt` -> `Proposed_Payment_Homeowners_Insurance__c`
- `homeownersAssociationDuesAndCondominiumFeesMonthlyAmt` -> `Proposed_Payment_HOA_Dues__c`
- `floodInsuranceMonthlyAmt` -> `Proposed_Payment_Other_Expenses__c`
- `totalMonthlyHousingExpenseAmt` -> `Total_Proposed_Monthly_Payment__c`
- `subjectProperty` address fields -> `Property_Address__c`
- `loanProcessorEmail` -> `Processor_Email__c`
- `brokerFee` / `grossLoanRevenue` -> `Listed_Revenue__c`
- first `BrokerFee` record (sum of payer amounts) -> `Broker_Compensation_Percentage__c`, `Listed_Revenue__c`
- `compensationType` / product compensation -> `Compensation_Type__c`
- fee IDs `1917` / `1889` with `feeBuilderArgs` -> `Broker_Compensation_String__c`
- property value fallback from `subjectProperty.estimatedValue` / `purchasePriceOrEstimatedValue` / `subjectProperty.salesContractAmt` -> `Property_Value__c`
- Primary borrower fields -> borrower name/email/phone/ssn/dob/marital status fields
- Co-borrower fields -> co-borrower name/email/phone/dob fields
- Co-borrower presence -> `HasCoborrowerc__c`, `CoBorrowerAuthorizedCreditReportc__c`, `CoBorrowerMortgageServicec__c`
- Defaults -> `Compensation_Type__c`, `Broker_Compensation_Percentage__c`, `API_Details__c`

## Related objects currently synced

- `fees[]` -> `Fee__c` (update-or-create by `Sonar_Fee_ID__c` + RLA)
- `conditions[]` -> `Condition__c` (update-or-create by `Sonar_Condition_ID__c` + `Sonar_Loan_ID__c`)

## Not yet parity with Apex

- Exact borrower-pair selection strategy (`borrowerPairs` order rules).
- Processor-email conflict logic against existing Salesforce value.
- Flat-fee deduction rules for compensation percentage by lender/date/transaction.
- Document, note, and milestone mapping logic.
- Credit report / contact / mailing-address sync behaviors.
- Detailed duplicate-handling diagnostics and retry/circuit-breaker controls.

## Next payloads

When you provide additional payload samples, we will:

1. append new source fields to this matrix,
2. mark field-level status (mapped/partial/unmapped),
3. expand the sync service to maintain backward compatibility.
