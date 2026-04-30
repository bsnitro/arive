# Arive → RLA Field Mapping (Funded Sample)

**Mapping handler:** [`services/salesforce-loan-handler.ts`](../services/salesforce-loan-handler.ts)  
**Arive source payload:** [`docs/Arivefundedsample.json`](Arivefundedsample.json)  
**Sonar reference payload:** [`docs/SonarFundedSample.json`](SonarFundedSample.json)  
**Salesforce RLA extract:** [`docs/RLAFundedSample`](RLAFundedSample)

**Legend:**
- `MAPPED` = actively written to Salesforce in the sync service
- `PARTIAL` = indirect/fallback/derived mapping; not full parity with Apex equivalent
- `TODO` = can be mapped now with available data — add to service
- `NEEDS INFO` = mapping requires clarification before implementing
- `NOT APPLICABLE` = no corresponding Salesforce target field exists

---

## Cross-source parity checkpoints

- `RLA.ApplicationExtIdentifier__c`: `OR010536-01` (matches Sonar `loanNumber`; Arive does not currently provide this value directly)
- `RLA.Sonar_GUID__c`: `OR010536-01_11051` (does not match Sonar top-level `loanId=10536`; indicates legacy/multi-source transformation in prior flow)
- `RLA.LOS_ID__c`: blank in extract (new integration now writes this from Arive loan id, but legacy sample does not have it)
- `RLA.Loan_Number__c`: `7000184826` (not Sonar `loanNumber` and not Arive `lenderLoanIdentifier` in this sample, which is blank)
- `RLA.Status__c`: `Closed` and `RLA.LoanSubStatus__c`: `Loan has been funded` (source payloads shown are `APPLICATION_INTAKE` / `Withdrawn`, so historical Salesforce updates occurred after initial ingest)
- `RLA.Total_Loan_Amount__c`: `394900` (matches Arive funded sample)
- `RLA.Proposed_Payment_First_Mortgage__c`: `2180.65` (matches Arive funded sample)
- `RLA.Top_End_Debt_to_Income__c`: `0.265128` (close to Arive `backEndDTI=26.51%`, represented as decimal in Salesforce)

Implication: this RLA extract represents a lifecycle-updated loan, not a pure single-event snapshot. Mapping work should focus on deterministic field translation rules, then milestone/status evolution logic separately.

---

## ✅ DONE — Top-level fields

- `ariveLoanId` → `LOS_ID__c` (fallback source)
- `sysGUID` → `ApplicationExtIdentifier__c`, `Sonar_GUID__c` (fallback identifier source)
- `industryChannel` → `Channelc__c` (`Broker` → `Brokered`)
- `createDateTime` → `Date_File_Started__c`, `Application_Date__c` (fallback source)
- `currentLoanStatus` → `Status__c`, `LoanSubStatus__c`, `LoanSubStatusDate__c`
- `loanPurpose` → `LoanPurpose__c`
- `mortgageType` → `Loan_Product__c` (translation logic applied)
- `purchasePriceOrEstimatedValue` → `Property_Value__c` (fallback)
- `lenderName` → `Lender_Name__c` (translation logic applied)
- `homeownersInsuranceMonthlyAmt` → `Proposed_Payment_Homeowners_Insurance__c`
- `mIPremiumMonthlyAmt` → `Proposed_Payment_Mortgage_Insurance__c`
- `homeownersAssociationDuesAndCondominiumFeesMonthlyAmt` → `Proposed_Payment_HOA_Dues__c`
- `realEstateTaxMonthlyAmt` → `Proposed_Payment_Property_Taxes__c`
- `totalMonthlyHousingExpenseAmt` → `Total_Proposed_Monthly_Payment__c`
- `firstMortgagePrincipalAndInterestMonthlyAmt` → `Proposed_Payment_First_Mortgage__c`
- `floodInsuranceMonthlyAmt` → `Proposed_Payment_Other_Expenses__c`
- `amortizationTerm` → `Loan_Term_Months__c` (fallback)
- `compensationType` → `Compensation_Type__c`
- `compensation` → `Compensation_Type__c` (fallback text source)
- `lockStatus` → `Lock_Status__c` (`None` → `Not Locked`)
- `lenderLoanIdentifier` → `Loan_Number__c` (fallback)
- `amortizationType` → `loanAmortizationType__c`
- `lienPosition` → `Lien_Position_Number__c`, `Lien_Position__c`
- `lenderProductName` → `Additional_Loan_Product_Details__c`
- `loanTerm` → `Loan_Term_Months__c`
- `ltv` → `Loan_to_Value__c`
- `cltv` → `Combined_Loan_to_Value__c`
- `brokerFee` → `Listed_Revenue__c` (fallback)
- `grossLoanRevenue` → `Listed_Revenue__c` (secondary fallback)
- `noteRate` → `InterestRate__c` (converted to percentage)
- `totalLoanAmount` → `Total_Loan_Amount__c`
- `backEndDTI` → `Top_End_Debt_to_Income__c`
- `loanOriginatorEmail` → `Loan_Officer_Email__c` (fallback: `loanTeam[role=Originator].emailAddressText`)
- `loanOriginatorName` → `Loan_Officer__c` (fallback: `loanTeam[role=Originator]` first+last name)
- `loanOfficerAssistantEmail` → `Loan_Officer_Assistant_Email__c`
- `loanProcessorEmail` → `Processor_Email__c` (fallback: `loanTeam[role=Processor].emailAddressText`)
- `fico` → `Credit_Score__c`
- `discountPoints` → `Expected_Discount_Rebate_Points__c`
- `impoundWaiver` → `Waive_Escrows__c` (string passthrough)
- `refinanceType` → `RefinanceType__c` (values: `"No Cash Out"`, `"Limited Cash Out"`, `"Cash Out"`; null for non-refi loans)
- `cashoutPurpose` → `Refinance_Purpose__c` (string passthrough)
- `loanBorrowers[primary].workPhone` → `BorrowerCellc__c`
- `loanBorrowers[primary].applicantType` → `BorrowerTypec__c`

## ✅ DONE — `subjectProperty`

- `subjectProperty.addressLineText` → `Property_Address__c` (combined) + primary component
- `subjectProperty.city` → `Property_City__c` AND `Property_Address__c` (combined)
- `subjectProperty.state` → `Property_State__c` AND `Property_Address__c` (combined)
- `subjectProperty.postalCode` → `Property_Zip_Code__c` AND `Property_Address__c` (combined)
- `subjectProperty.salesContractAmt` → `Property_Value__c` (fallback)
- `subjectProperty.propertyUsageType` → `Occupancy_Type__c`

## ✅ DONE — `keyDates`

- `keyDates.tridDate` → `Application_Date__c` (fallback)
- `keyDates.appraisalOrderedDate` → `Appraisal_Order_Date__c`
- `keyDates.appraisalDeliveryDate` → `Appraisal_Received_Date__c`
- `keyDates.appraisalContingency` → `Appraisal_Contingency_Date__c`
- `keyDates.titleOrderedDate` → `Title_Order_Date__c`
- `keyDates.initialLESentDate` → `Disclosures_Sent_Date__c`
- `keyDates.initialCDSentDate` / `mostRecentCDSentDate` → `Closing_Disclosure_Send_Date__c` (most recent wins)
- `keyDates.initialCDSignedDate` → `Initial_CD_Signature_Date__c`
- `keyDates.estimatedFundingDate` → `Estimated_Closing_Date__c`
- `keyDates.closingContingency` → `Closing_Date__c`
- `keyDates.intentToProceedDate` → `Intent_to_Proceed_Received__c` (boolean: set `true` when date is present)
- `keyDates.salesContractDate` → `Purchase_Contract_Date__c`
- `keyDates.creditOrderDate` → `Initial_Credit_Pull_Date__c`
- `keyDates.creditExpirationDate` → `Last_Credit_Pull_Date__c`

## ✅ DONE — `currentLoanStatus`

- `currentLoanStatus.status` → `Status__c`
- `currentLoanStatus.adverseReason` → `LoanSubStatus__c`
- `currentLoanStatus.date` → `LoanSubStatusDate__c`

## ✅ DONE — `loanBorrowers[]` (borrower)

- `loanBorrowers[].firstName` → `BorrowerFirstNamec__c`
- `loanBorrowers[].lastName` → `BorrowerLastNamec__c`
- `loanBorrowers[].emailAddressText` → `BorrowerEmailc__c`
- `loanBorrowers[].maritalStatusType` → `BorrowerMaritalStatusc__c`
- `loanBorrowers[].mobilePhone10digit` → `BorrowerHomePhonec__c`
- `loanBorrowers[].birthDate` → `Borrowerdobc__c`
- `loanBorrowers[].firstTimeHomeBuyer` → `First_Time_Homebuyer__c` (fallback)

## ✅ DONE — `loanBorrowers[]` (co-borrower)

- `loanBorrowers[co].firstName` → `CoBorrowerFirstNamec__c`
- `loanBorrowers[co].lastName` → `CoBorrowerLastNamec__c`
- `loanBorrowers[co].emailAddressText` → `CoBorrowerEmailc__c`
- `loanBorrowers[co].mobilePhone10digit` → `CoBorrowerCellc__c`
- `loanBorrowers[co].workPhone` → `CoBorrowerHomePhonec__c`
- `loanBorrowers[co].maritalStatusType` → `CoBorrowerMaritalStatusc__c`
- `loanBorrowers[co].birthDate` → `CoBorrowerdobc__c`
- co-borrower presence → `HasCoborrowerc__c`, `CoBorrowerAuthorizedCreditReportc__c`, `CoBorrowerMortgageServicec__c`

## ✅ DONE — Related objects

- `fees[]` → `Fee__c` records (full sync logic; `loanFeeId`, `feeType`, `section`, payer amounts)
- `conditions[]` → `Condition__c` records (full sync logic; `loanConditionId`, `name`, `description`, `scope`, `type`, `completed`, `critical`)

---

## 🔧 TODO — Add to service now (data available, target field confirmed)

All of these have clear source values in Arive and confirmed target fields in the RLA extract. Add to [`services/salesforce-loan-handler.ts`](../services/salesforce-loan-handler.ts).

### Top-level fields

- `interestOnlyInd` → `IsInterestOnly__c` (boolean passthrough; Arive: `false`, RLA: `IsInterestOnly__c`)
- `interestOnlyTermMonthsCount` → `IntOnlyTermMonthCount__c` (numeric)
- `hcltv` → `High_Credit_Loan_to_Value__c` (same `toSalesforcePercent` conversion as `ltv`/`cltv`; Arive: `100`, target: decimal)
- `downPayment` → `Subject_Property_Down_Payment_Amount__c` (numeric; Arive: `0`)
- `sellerCredit` → `Seller_Credit_Amount__c` (numeric; Arive: `10000`)
- `lockDate` → `Lock_Date__c` (date; Arive: `null` in sample, RLA: `2026-02-18`)
- `lockExpirationDate` → `Lock_Expiration_Date__c` (date; Arive: `null` in sample, RLA: `2026-03-20`)
- `initialFixedPeriodEffectiveMonthsCount` → `MonthsBeforeFirstAdj__c` (numeric; Arive: `360`)
- `normalRateAdjustmentPeriod` → `MonthsBetweenAdjustments__c` (numeric; Arive: `null`)
- `frontEndDTI` → `Bottom_End_Debt_to_Income__c` (same `toSalesforcePercent` conversion as `backEndDTI`; Arive: `14.19`)
- `prepayPenalty` → `HasPrepaymentPenalty__c` (translation: `"No"` → `false`, `"Yes"` → `true`; Arive: `"No"`, RLA: `false`)
- `settlementNumber` → `Settlement_Case_Number__c` (string; Arive: `null`)

### `subjectProperty` fields

- `subjectProperty.county` → `Subject_Property_County__c` (string; Arive: `"CUMBERLAND"`, RLA: `"Cumberland"`)
- `subjectProperty.addressUnitIdentifier` → `Subject_Property_Unit_Number__c` (string; Arive: `null`)
- `subjectProperty.financedUnitCount` → `Subject_Property_Number_of_Units__c` (numeric; Arive: `1`, RLA: `1`)
- `subjectProperty.housingType` → `Property_Type__c` (string passthrough; Arive: `"SingleFamily"`)


### `keyDates` fields

- `keyDates.salesContractDate` → `Purchase_Contract_Date__c` (date; Arive: `null` in sample, RLA: `2026-02-16`)
- `keyDates.initialCDSignedDate` → `Initial_CD_Signature_Date__c` (date; Arive: `null` in sample, RLA: `2026-03-09`)
- `keyDates.creditOrderDate` → `Initial_Credit_Pull_Date__c` (date)
- `keyDates.creditExpirationDate` → `Last_Credit_Pull_Date__c` (date)

---


---

## ⏭️ NOT APPLICABLE — No Salesforce target field or cannot be written via API

- `loanOriginatorPhone` → no RLA string field for LO phone
- `loanProcessorName` → `Loan_Processor_Name__c` is a calculated field in RLA; cannot be written directly
- `loanOfficerAssistantName` → `Assistant__c` is a Contact lookup; no writable string name field
- `loanBorrowers[].currentResidence.*` → `Borrower_Address__c` is a calculated HTML field; no writable individual address fields visible
- `loanBorrowers[].borrowerIncome` → no RLA target field
- `loanBorrowers[].employments[].*` → no RLA target fields for employment detail
- `employment[]` (top-level) → same as above
- `earnestMoneyDeposit` → no RLA field visible
- `CreditReportFee` → no clear RLA target
- `leadSource` → `Lead_Source__c` is calculated in RLA from Contact/Opportunity; cannot be written
- `documentationType` → no RLA field
- `deepLinkURL` → no RLA field
- `orgUnitDisplayName` / `orgUnitId` → no RLA field
- `keyDates.hoiOrderedDate` / `hoiReceivedDate` → no RLA date fields for HOI
- `keyDates.taxTranscriptOrderedDate` / `taxTranscriptReceivedDate` → no RLA fields
- `keyDates.titleReceivedDate` → no separate RLA title received date field
- `keyDates.loanContingency` → no RLA field
- `keyDates.firstPaymentDate` → no RLA field
- `keyDates.initialLESignedDate` / `mostRecentLESentDate` / `mostRecentLESignedDate` / `mostRecentCDSignedDate` → no RLA fields
- `keyDates.preApprovalExpiryDate` / `dateToAvoidEPO` → no RLA fields
- `sysGUID` → already used for identifier derivation, no additional direct field
- `referralContactSourceName` / `referralContactSourceEmail` → no RLA field
- `loanCreatedFrom` → no RLA field
- `subjectTBDIndicator` → no RLA field
- `modifiedDateTime` → no writable RLA equivalent
- `creditRepairIndicator` → no RLA field
- `archiveIndicator` / `archiveDate` → no RLA field
- `mersNumberforNonDel` → no RLA field
- `principalInterestAndPMI` → same value as `firstMortgagePrincipalAndInterestMonthlyAmt` (already mapped)
- `buyDown` → `IsTempIntRateBuydown__c` is boolean; Arive sends string `""` — no clean mapping
- `financedFees` → already handled via fee deduction in `calculateLoanAmountAfterFees`
- `reimbursements` → no RLA field
- `toleranceCures` → no RLA field
- `netLoanRevenue` → no RLA field
- `crmReferenceId` → no RLA field
- `loanBorrowers[].nickName` → no RLA field
- `loanBorrowers[].borrowerPairLoanAppSequence` → no RLA field
- `loanBorrowers[].dayOfBirth` / `monthOfBirth` → redundant with `birthDate` (already mapped)
- `loanBorrowers[].posAppSubmissionDate` → no RLA field
- `loanBorrowers[].preferedLanguages` → no RLA field
- `loanBorrowers[].currentResidence.addressCountry` → no RLA field
- `loanBorrowers[].currentResidence.durationTermMonths` → no RLA field
- `loanBorrowers[].currentResidence.residencyBasisType` → no RLA field
- `businessContacts` → no RLA field
- `loanStatusHistory[]` → not currently used for milestone/date derivation
- `loanTrackers[]` → tracker status names don't map directly to RLA date fields without confirmed mapping (see NEEDS INFO above)
- `estCashToClose` → no RLA field
- `orgUnitId` → no RLA field (unless confirmed otherwise)
- `leadProvidedBy` → no RLA field
- `purchaseDate` → no RLA field distinct from `salesContractDate`
- `receivedPayments` → no RLA field

---

## Remaining open items

- **Borrower-pair identity logic**: `borrowerPairs` from Sonar vs position-based borrower mapping in current code
- **Processor email conflict logic**: preserve existing Salesforce value unless Arive has a processor team member
- **Flat-fee deduction rules**: broker compensation percentage by lender/date/transaction
- **Milestone/status/date derivation**: from `loanTrackers[]`, `loanStatusHistory[]`, notes, documents
- **Contact and mailing-address update behavior**
- **FICO sourcing**: `fico` is null in Arive APPLICATION_INTAKE payloads — confirm whether it populates in later lifecycle events or only comes from a separate credit pull
