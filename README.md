# SevDesk API - Kotlin

This is a simple SevDesk Rest API Client for Kotlin.

# Current supported endpoints:

## Basics
- [x] [Retrieve bookkeeping system version](https://api.sevdesk.de/#tag/Basics/operation/bookkeepingSystemVersion)

## CheckAccount
- [x] [Retrieve check accounts](https://api.sevdesk.de/#tag/CheckAccount/operation/getCheckAccounts)
- [x] [Create a new file import account](https://api.sevdesk.de/#tag/CheckAccount/operation/createFileImportAccount)
- [x] [Create a new clearing account](https://api.sevdesk.de/#tag/CheckAccount/operation/createClearingAccount)
- [x] [Find check account by ID](https://api.sevdesk.de/#tag/CheckAccount/operation/getCheckAccountById)
- [x] [Update an existing check account](https://api.sevdesk.de/#tag/CheckAccount/operation/updateCheckAccount)
- [x] [Deletes a check account](https://api.sevdesk.de/#tag/CheckAccount/operation/deleteCheckAccount)
- [x] [Get the balance at a given date](https://api.sevdesk.de/#tag/CheckAccount/operation/getBalanceAtDate)

## CheckAccountTransaction
- [x] [Retrieve transactions](https://api.sevdesk.de/#tag/CheckAccountTransaction/operation/getTransactions)
- [x] [Create a new transaction](https://api.sevdesk.de/#tag/CheckAccountTransaction/operation/createTransaction)
- [x] [Find check account transaction by ID](https://api.sevdesk.de/#tag/CheckAccountTransaction/operation/getCheckAccountTransactionById)
- [x] [Update an existing check account transaction](https://api.sevdesk.de/#tag/CheckAccountTransaction/operation/updateCheckAccountTransaction)
- [x] [Deletes a check account transaction](https://api.sevdesk.de/#tag/CheckAccountTransaction/operation/deleteCheckAccountTransaction)
- [x] [Enshrine](https://api.sevdesk.de/#tag/CheckAccountTransaction/operation/checkAccountTransactionEnshrine)

## AccountingContact
- [ ] [Retrieve accounting contact](https://api.sevdesk.de/#tag/AccountingContact/operation/getAccountingContact)
- [ ] [Create a new accounting contact](https://api.sevdesk.de/#tag/AccountingContact/operation/createAccountingContact)
- [ ] [Find accounting contact by ID](https://api.sevdesk.de/#tag/AccountingContact/operation/getAccountingContactById)
- [ ] [Update an existing accounting contact](https://api.sevdesk.de/#tag/AccountingContact/operation/updateAccountingContact)
- [ ] [Deletes an accounting contact](https://api.sevdesk.de/#tag/AccountingContact/operation/deleteAccountingContact)

## CommunicationWay
- [x] [Retrieve communication ways](https://api.sevdesk.de/#tag/CommunicationWay/operation/getCommunicationWays)
- [x] [Create a new contact communication way](https://api.sevdesk.de/#tag/CommunicationWay/operation/createCommunicationWay)
- [x] [Find communication way by ID](https://api.sevdesk.de/#tag/CommunicationWay/operation/getCommunicationWayById)
- [x] [Deletes a communication way](https://api.sevdesk.de/#tag/CommunicationWay/operation/deleteCommunicationWay)
- [x] [Update a existing communication way](https://api.sevdesk.de/#tag/CommunicationWay/operation/UpdateCommunicationWay)
- [x] [Retrieve communication way keys](https://api.sevdesk.de/#tag/CommunicationWay/operation/getCommunicationWayKeys)

## ContactAddress
- [x] [Create a new contact address](https://api.sevdesk.de/#tag/ContactAddress/operation/createContactAddress)
- [x] [Retrieve contact addresses](https://api.sevdesk.de/#tag/ContactAddress/operation/getContactAddresses)
- [x] [Find contact address by ID](https://api.sevdesk.de/#tag/ContactAddress/operation/contactAddressId)
- [x] [Update a existing contact address](https://api.sevdesk.de/#tag/ContactAddress/operation/updateContactAddress)
- [x] [Deletes a contact address](https://api.sevdesk.de/#tag/ContactAddress/operation/deleteContactAddress)

## Contact
- [x] [Get next free customer number](https://api.sevdesk.de/#tag/Contact/operation/getNextCustomerNumber)
- [x] [Find contacts by custom field value](https://api.sevdesk.de/#tag/Contact/operation/findContactsByCustomFieldValue)
- [x] [Check if a customer number is available](https://api.sevdesk.de/#tag/Contact/operation/contactCustomerNumberAvailabilityCheck)
- [x] [Retrieve contacts](https://api.sevdesk.de/#tag/Contact/operation/getContacts)
- [x] [Create a new contact](https://api.sevdesk.de/#tag/Contact/operation/createContact)
- [x] [Find contact by ID](https://api.sevdesk.de/#tag/Contact/operation/getContactById)
- [x] [Update a existing contact](https://api.sevdesk.de/#tag/Contact/operation/updateContact)
- [x] [Deletes a contact](https://api.sevdesk.de/#tag/Contact/operation/deleteContact)
- [x] [Get number of all items](https://api.sevdesk.de/#tag/Contact/operation/getContactTabsItemCountById)

## ContactField
- [ ] [Retrieve Placeholders](https://api.sevdesk.de/#tag/ContactField/operation/getPlaceholder)
- [x] [Retrieve contact fields](https://api.sevdesk.de/#tag/ContactField/operation/getContactFields)
- [x] [Create contact field](https://api.sevdesk.de/#tag/ContactField/operation/createContactField)
- [x] [Retrieve contact fields](https://api.sevdesk.de/#tag/ContactField/operation/getContactFieldsById)
- [x] [Update a contact field](https://api.sevdesk.de/#tag/ContactField/operation/updateContactfield)
- [x] [Delete a contact field](https://api.sevdesk.de/#tag/ContactField/operation/deleteContactCustomFieldId)
- [x] [Retrieve contact field settings](https://api.sevdesk.de/#tag/ContactField/operation/getContactFieldSettings)
- [x] [Create contact field setting](https://api.sevdesk.de/#tag/ContactField/operation/createContactFieldSetting)
- [x] [Find contact field setting by ID](https://api.sevdesk.de/#tag/ContactField/operation/getContactFieldSettingById)
- [x] [Update contact field setting](https://api.sevdesk.de/#tag/ContactField/operation/updateContactFieldSetting)
- [x] [Deletes a contact field setting](https://api.sevdesk.de/#tag/ContactField/operation/deleteContactFieldSetting)
- [x] [Receive count reference](https://api.sevdesk.de/#tag/ContactField/operation/getReferenceCount)

## CreditNote
- [ ] [Retrieve CreditNote](https://api.sevdesk.de/#tag/CreditNote/operation/getCreditNotes)
- [ ] [Create a new creditNote](https://api.sevdesk.de/#tag/CreditNote/operation/createcreditNote)
- [ ] [Creates a new creditNote from an invoice](https://api.sevdesk.de/#tag/CreditNote/operation/createCreditNoteFromInvoice)
- [ ] [Creates a new creditNote from a voucher](https://api.sevdesk.de/#tag/CreditNote/operation/createCreditNoteFromVoucher)
- [ ] [Find creditNote by ID](https://api.sevdesk.de/#tag/CreditNote/operation/getcreditNoteById)
- [ ] [Update an existing creditNote](https://api.sevdesk.de/#tag/CreditNote/operation/updatecreditNote)
- [ ] [Deletes an creditNote](https://api.sevdesk.de/#tag/CreditNote/operation/deletecreditNote)
- [ ] [Send credit note by printing](https://api.sevdesk.de/#tag/CreditNote/operation/sendCreditNoteByPrinting)
- [ ] [Mark credit note as sent](https://api.sevdesk.de/#tag/CreditNote/operation/creditNoteSendBy)
- [ ] [Enshrine](https://api.sevdesk.de/#tag/CreditNote/operation/creditNoteEnshrine)
- [ ] [Retrieve pdf document of a credit note](https://api.sevdesk.de/#tag/CreditNote/operation/creditNoteGetPdf)
- [ ] [Send credit note via email](https://api.sevdesk.de/#tag/CreditNote/operation/sendCreditNoteViaEMail)
- [ ] [Book a credit note](https://api.sevdesk.de/#tag/CreditNote/operation/bookCreditNote)
- [ ] [Reset status to open](https://api.sevdesk.de/#tag/CreditNote/operation/creditNoteResetToOpen)
- [ ] [Reset status to draft](https://api.sevdesk.de/#tag/CreditNote/operation/creditNoteResetToDraft)

## CreditNotePos
- [ ] [Retrieve creditNote positions](https://api.sevdesk.de/#tag/CreditNotePos/operation/getcreditNotePositions)

## Export
- [ ] [Update export config](https://api.sevdesk.de/#tag/Export/operation/updateExportConfig)
- [ ] [Export datev](https://api.sevdesk.de/#tag/Export/operation/exportDatevDepricated) -> Deprecated
- [ ] [Start DATEV CSV ZIP export](https://api.sevdesk.de/#tag/Export/operation/exportDatevCSV)
- [ ] [Start DATEV XML ZIP export](https://api.sevdesk.de/#tag/Export/operation/exportDatevXML)
- [ ] [Generate download hash](https://api.sevdesk.de/#tag/Export/operation/generateDownloadHash)
- [ ] [Get progress](https://api.sevdesk.de/#tag/Export/operation/getProgress)
- [ ] [Get job download info](https://api.sevdesk.de/#tag/Export/operation/jobDownloadInfo)
- [x] [Export invoice](https://api.sevdesk.de/#tag/Export/operation/exportInvoice)
- [x] [Export Invoice as zip](https://api.sevdesk.de/#tag/Export/operation/exportInvoiceZip)
- [ ] [Export creditNote](https://api.sevdesk.de/#tag/Export/operation/exportCreditNote)
- [ ] [Export voucher as zip](https://api.sevdesk.de/#tag/Export/operation/exportVoucher)
- [ ] [Export transaction](https://api.sevdesk.de/#tag/Export/operation/exportTransactions)
- [ ] [Export voucher zip](https://api.sevdesk.de/#tag/Export/operation/exportVoucherZip)
- [ ] [Export contact](https://api.sevdesk.de/#tag/Export/operation/exportContact)

## Part
- [ ] [Retrieve parts](https://api.sevdesk.de/#tag/Part/operation/getParts)
- [ ] [Create a new part](https://api.sevdesk.de/#tag/Part/operation/createPart)
- [ ] [Find part by ID](https://api.sevdesk.de/#tag/Part/operation/getPartById)
- [ ] [Update an existing part](https://api.sevdesk.de/#tag/Part/operation/updatePart)
- [ ] [Get stock of a part](https://api.sevdesk.de/#tag/Part/operation/partGetStock)

## Invoice
- [x] [Retrieve invoices](https://api.sevdesk.de/#tag/Invoice/operation/getInvoices)
- [ ] [Create a new invoice](https://api.sevdesk.de/#tag/Invoice/operation/createInvoiceByFactory)
- [ ] [Find invoice by ID](https://api.sevdesk.de/#tag/Invoice/operation/getInvoiceById)
- [ ] [Find invoice positions](https://api.sevdesk.de/#tag/Invoice/operation/getInvoicePositionsById)
- [ ] [Create invoice from order](https://api.sevdesk.de/#tag/Invoice/operation/createInvoiceFromOrder)
- [ ] [Create invoice reminder](https://api.sevdesk.de/#tag/Invoice/operation/createInvoiceReminder)
- [ ] [Check if an invoice is already partially paid](https://api.sevdesk.de/#tag/Invoice/operation/getIsInvoicePartiallyPaid)
- [ ] [Cancel an invoice / Create cancellation invoice](https://api.sevdesk.de/#tag/Invoice/operation/cancelInvoice)
- [ ] [Render the pdf document of an invoice](https://api.sevdesk.de/#tag/Invoice/operation/invoiceRender)
- [ ] [Send invoice via email](https://api.sevdesk.de/#tag/Invoice/operation/sendInvoiceViaEMail)
- [x] [Retrieve pdf document of an invoice](https://api.sevdesk.de/#tag/Invoice/operation/invoiceGetPdf)
- [ ] [Retrieve XML of an e-invoice](https://api.sevdesk.de/#tag/Invoice/operation/invoiceGetXml)
- [x] [Mark invoice as sent](https://api.sevdesk.de/#tag/Invoice/operation/invoiceSendBy)
- [ ] [Enshrine](https://api.sevdesk.de/#tag/Invoice/operation/invoiceEnshrine)
- [ ] [Book an invoice](https://api.sevdesk.de/#tag/Invoice/operation/bookInvoice)
- [ ] [Reset status to open](https://api.sevdesk.de/#tag/Invoice/operation/invoiceResetToOpen)
- [ ] [Reset status to draft](https://api.sevdesk.de/#tag/Invoice/operation/invoiceResetToDraft)

## InvoicePos
- [ ] [Retrieve InvoicePos](https://api.sevdesk.de/#tag/InvoicePos/operation/getInvoicePos)

## Layout
- [ ] [Retrieve letterpapers](https://api.sevdesk.de/#tag/Layout/operation/getLetterpapersWithThumb)
- [ ] [Retrieve templates](https://api.sevdesk.de/#tag/Layout/operation/getTemplates)
- [ ] [Update an invoice template](https://api.sevdesk.de/#tag/Layout/operation/updateInvoiceTemplate)
- [ ] [Update an order template](https://api.sevdesk.de/#tag/Layout/operation/updateOrderTemplate)
- [ ] [Update an of credit note template](https://api.sevdesk.de/#tag/Layout/operation/updateCreditNoteTemplate)

## Order
- [ ] [Retrieve orders](https://api.sevdesk.de/#tag/Order/operation/getOrders)
- [ ] [Create a new order](https://api.sevdesk.de/#tag/Order/operation/createOrder)
- [ ] [Find order by ID](https://api.sevdesk.de/#tag/Order/operation/getOrderById)
- [ ] [Update an existing order](https://api.sevdesk.de/#tag/Order/operation/updateOrder)
- [ ] [Deletes an order](https://api.sevdesk.de/#tag/Order/operation/deleteOrder)
- [ ] [Find order positions](https://api.sevdesk.de/#tag/Order/operation/getOrderPositionsById)
- [ ] [Find order discounts](https://api.sevdesk.de/#tag/Order/operation/getDiscounts)
- [ ] [Find related objects](https://api.sevdesk.de/#tag/Order/operation/getRelatedObjects)
- [ ] [Send order via email](https://api.sevdesk.de/#tag/Order/operation/sendorderViaEMail)
- [ ] [Create packing list from order](https://api.sevdesk.de/#tag/Order/operation/createPackingListFromOrder)
- [ ] [Create contract note from order](https://api.sevdesk.de/#tag/Order/operation/createContractNoteFromOrder)
- [ ] [Retrieve pdf document of an order](https://api.sevdesk.de/#tag/Order/operation/orderGetPdf)
- [ ] [Mark order as sent](https://api.sevdesk.de/#tag/Order/operation/orderSendBy)

## OrderPos
- [ ] [Retrieve order positions](https://api.sevdesk.de/#tag/OrderPos/operation/getOrderPositions)
- [ ] [Find order position by ID](https://api.sevdesk.de/#tag/OrderPos/operation/getOrderPositionById)
- [ ] [Update an existing order position](https://api.sevdesk.de/#tag/OrderPos/operation/updateOrderPosition)
- [ ] [Deletes an order Position](https://api.sevdesk.de/#tag/OrderPos/operation/deleteOrderPos)

## Voucher
- [ ] [Create a new voucher](https://api.sevdesk.de/#tag/Voucher/operation/voucherFactorySaveVoucher)
- [ ] [Upload voucher file](https://api.sevdesk.de/#tag/Voucher/operation/voucherUploadFile)
- [ ] [Retrieve vouchers](https://api.sevdesk.de/#tag/Voucher/operation/getVouchers)
- [ ] [Find voucher by ID](https://api.sevdesk.de/#tag/Voucher/operation/getVoucherById)
- [ ] [Update an existing voucher](https://api.sevdesk.de/#tag/Voucher/operation/updateVoucher)
- [ ] [Enshrine](https://api.sevdesk.de/#tag/Voucher/operation/voucherEnshrine)
- [ ] [Book a voucher](https://api.sevdesk.de/#tag/Voucher/operation/bookVoucher)
- [ ] [Reset status to open](https://api.sevdesk.de/#tag/Voucher/operation/voucherResetToOpen)
- [ ] [Reset status to draft](https://api.sevdesk.de/#tag/Voucher/operation/voucherResetToDraft)
- [ ] [Get all account guides](https://api.sevdesk.de/#tag/Voucher/operation/forAllAccounts)
- [ ] [Get guidance by account number](https://api.sevdesk.de/#tag/Voucher/operation/forAccountNumber)
- [ ] [Get guidance by Tax Rule](https://api.sevdesk.de/#tag/Voucher/operation/forTaxRule)
- [ ] [Get guidance for revenue accounts](https://api.sevdesk.de/#tag/Voucher/operation/forRevenue)
- [ ] [Get guidance for expense accounts](https://api.sevdesk.de/#tag/Voucher/operation/forExpense)

## VoucherPos
- [ ] [Retrieve voucher positions](https://api.sevdesk.de/#tag/VoucherPos/operation/getVoucherPositions)

## Report
- [ ] [Export invoice list](https://api.sevdesk.de/#tag/Report/operation/reportInvoice)
- [ ] [Export order list](https://api.sevdesk.de/#tag/Report/operation/reportOrder)
- [ ] [Export contact list](https://api.sevdesk.de/#tag/Report/operation/reportContact)
- [ ] [Export voucher list](https://api.sevdesk.de/#tag/Report/operation/reportVoucher)

## Tag
- [ ] [Retrieve tags](https://api.sevdesk.de/#tag/Tag/operation/getTags)
- [ ] [Find tag by ID](https://api.sevdesk.de/#tag/Tag/operation/getTagById)
- [ ] [Update tag](https://api.sevdesk.de/#tag/Tag/operation/updateTag)
- [ ] [Deletes a tag](https://api.sevdesk.de/#tag/Tag/operation/deleteTag)
- [ ] [Create a new tag](https://api.sevdesk.de/#tag/Tag/operation/createTag)
- [ ] [Retrieve tag relations](https://api.sevdesk.de/#tag/Tag/operation/getTagRelations)

## Other
- [x] [Get Static Countrys](https://my.sevdesk.de/api/v1/StaticCountry)