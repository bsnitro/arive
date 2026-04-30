# Arive -> RLA Date Mapping Worksheet

**Mapping handler:** `services/salesforce-loan-handler.ts`  
**Source object:** `loan.keyDates` from `GET /loans/{sysGUID}`  
**Target object:** `ResidentialLoanApplication__c`

This worksheet reflects the **current implemented behavior** and the latest mapping decisions.

## Implemented mappings

| Arive field | Salesforce field | Rule |
|---|---|---|
| `appraisalOrderedDate` | `Appraisal_Order_Date__c` | Direct map |
| `appraisalDeliveryDate` | `Appraisal_Received_Date__c` | Direct map |
| `titleOrderedDate` | `Title_Order_Date__c` | Direct map |
| `appraisalContingency` | `Appraisal_Contingency_Date__c` | Direct map |
| `loanContingency` | `Approval_Contingency_Date__c` | Closest semantic match |
| `closingContingency` | `Closing_Date__c` | Direct map |
| `initialCDSignedDate` | `Initial_CD_Signature_Date__c` | Direct map |
| `salesContractDate` | `Purchase_Contract_Date__c` | Direct map |
| `creditOrderDate` | `Initial_Credit_Pull_Date__c` | Direct map |
| `creditExpirationDate` | `Last_Credit_Pull_Date__c` | Direct map |
| `applicationDate` or `createDateTime` | `Date_File_Started__c` | Top-level loan field fallback chain |
| `applicationDate` or `createDateTime` or `tridDate` | `Application_Date__c` | Top-level date first, then TRID fallback |
| `mostRecentLESentDate` or `initialLESentDate` | `Disclosures_Sent_Date__c` | Latest LE sent preferred |
| `revisedCDSentDate` or (`mostRecentCDSentDate` if initial exists) or `initialCDSentDate` | `Closing_Disclosure_Send_Date__c` | Go-forward CD sent precedence |
| `closingContingency` or `earliestClosingDate` or `estimatedFundingDate` | `Estimated_Closing_Date__c` | Earliest business target precedence |

## Trigger-specific date behavior

| Event trigger | Date-side behavior |
|---|---|
| `LOAN_CREATED` | Full payload mapping (all date rules above apply) |
| `LOAN_APP_SUBMITTED` | Full payload mapping + `milestoneCurrentName__c = "ApplicationSubmitted"` |
| `LOAN_STAGE_CHANGED` | Milestone-only update; if stage is pre-approval then set `Pre_Approval_Date__c` to current date |
| `LOAN_ARCHIVED` | No general date mapping; only `LoanSubStatus__c = "Archived in LOS"` |

## Intentionally not mapped (no confirmed RLA date target)

- `taxTranscriptOrderedDate`
- `taxTranscriptReceivedDate`
- `titleReceivedDate`
- `hoiOrderedDate`
- `hoiReceivedDate`
- `preApprovalExpiryDate`
- `initialLESignedDate`
- `mostRecentLESignedDate`
- `mostRecentCDSignedDate`
- `estFirstPaymentDate`
- `firstPaymentDate`
- `dateToAvoidEPO`
- `revisedLESentDate` (not needed per latest worksheet)
- `initialLEReceivedDate`
- `revisedLEReceivedDate`
- `initialCDReceivedDate`
- `revisedCDReceivedDate`

## Explicit removals from prior logic

- `intentToProceedDate` is **not** mapped to `Intent_to_Proceed_Received__c` anymore.

## Notes

- All mapped date values are normalized via `toDateValue()` to `YYYY-MM-DD`.
- Empty/invalid dates are written as `null`/`undefined` via existing payload behavior.
