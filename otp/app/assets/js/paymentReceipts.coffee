invoiceManager =
  init: ->
    console.log("Init invoiceManager")
    that = this
    
    $("#invoice-completer").typeahead([
      name: "invoices"
      valueKey: "id"
      remote: "/invoices/search/%QUERY"
      template: '<p class="idandname">{{id}} : {{customer.name}}</p><p class="amountanddate">{{amount}}; {{billingDate}}</p>'
      engine: Hogan     
    ]).bind "typeahead:selected", (e, invoice) ->
      that.invoiceId = invoice.id
      $("#details-row").removeClass("hidden")

      that.getCustomerDetails(invoice.customer.id)
      that.getInvoiceDetails(invoice.id)
      $("#invoiceamount").text(invoice.amount)
      $("#invoicedate").text(invoice.billingDate)

      that.resetPaymentReceiptStuff()
      $("#add-payment-receipt").click ->
        date = $("#pr-date").val()
        amount = $("#pr-amount").val()
        that.addPaymentReceipt(date, amount)

  resetPaymentReceiptStuff: ->
    $("#add-payment-receipt").unbind()
    $("#payment-receipts tbody").empty()

  addPaymentReceiptLine: (date, amount, id, needsDiscountAdjustment = false) ->
    that = this
    newline = $(
      """
      <tr>
        <td class="pr-line-date">#{date}</td>
        <td class="pr-line-amount">#{amount}</td>
        <td>
          <span class="id hidden">#{id}</span>
          <a class="delete">Delete</a>
        </td>
      </tr>""")

    $("#payment-receipts tbody").append newline
    newline.find(".id").text(id)

    if needsDiscountAdjustment
      this.updateDayBasedDiscount () -> 
        that.addNewlineDeleteHandler(newline, id)
    else
      this.updateTotalAmount()
      this.addNewlineDeleteHandler(newline, id)

  addNewlineDeleteHandler: (newline, paymentReceiptId) ->
    that = this
    newline.find(".delete").click ->
      deleteLine = this
      deleteUrl = $("#payment-receipts").attr("data-delete-url")
      $.ajax
        url: deleteUrl
        type: "POST"
        dataType: "json"
        contentType: "application/json; charset=utf-8"
        data:
          JSON.stringify
            prId: paymentReceiptId
        success: that.deletePaymentReceiptLine($(deleteLine))
          

  deletePaymentReceiptLine: (deleteLine) ->
    deleteLine.closest("tr").remove()
    this.updateDayBasedDiscount()

  updateDayBasedDiscount: (successHook = ->) ->
    that = this
    maxPaymentReceiptDate = that.getMaxPaymentReceiptDate()
    daysDiscountUrl = $("#invoice-details").attr("data-days-discount-url")
    $.ajax
      url: daysDiscountUrl
      type: "POST"
      dataType: "json"
      contentType: "application/json; charset=utf-8"
      data:
        JSON.stringify
          invoiceId: that.invoiceId
          maxPaymentReceiptDate: maxPaymentReceiptDate
      success: (data) ->
        $("#days-offset-discount").text(data.daysOffsetDiscount)
        that.applyDayBasedDisountFactor(data.daysOffsetDiscount)
        that.updateTotalAmount(true)
        successHook()

  updateTotalAmount: (paidStatusNeedsUpdate = false) ->
    totalAmount = 0
    $(".pr-line-amount").each (index, field) ->
      totalAmount += parseFloat($(field).text())
    $("#total-amount").text(totalAmount)
    remainingAmount = parseFloat($("#invoiceamount-discounted").text()) - totalAmount
    $("#remaining-amount").text(remainingAmount)

    if paidStatusNeedsUpdate
      if remainingAmount <= 0
        this.setPaid(true, ->
          $("#paidstatus").addClass("text-success").removeClass("text-danger").text("Paid")
        )
      else
        this.setPaid(false, ->
          $("#paidstatus").addClass("text-danger").removeClass("text-success").text("Not paid yet")
        )
    
  setPaid: (paid, callback) ->
    that = this
    setPaidUrl = $("#invoice-details").attr("data-set-paid-url")
    $.ajax
      url: setPaidUrl
      type: "POST"
      dataType: "json"
      contentType: "application/json; charset=utf-8"
      data:
        JSON.stringify
          invoiceId: that.invoiceId
          paid: paid
      success: (data) ->
        callback()

  addPaymentReceipt: (date, amount) ->
    that = this
    addUrl = $("#payment-receipts").attr("data-add-url")
    $.ajax
      url: addUrl
      type: "POST"
      dataType: "json"
      contentType: "application/json; charset=utf-8"
      data:
        JSON.stringify
          invoiceId: that.invoiceId
          amount: amount
          date: date
      success: (data, textStatus) ->
        that.addPaymentReceiptLine(date, amount, data.id, true)

  getCustomerDetails: (customerId) ->
    postUrl = $("#customer-details").attr("data-target")

    $.ajax
      url: postUrl
      type: "POST"
      dataType: "json"
      data:
        JSON.stringify
          customerId: customerId
      contentType: "application/json; charset=utf-8"
      success: (data, textStatus) ->
        $("#street").text(data.street)
        $("#streetno").text(data.streetNumber)
        $("#postcode").text(data.postCode)
        $("#city").text(data.city)
        $("#firstname").text(data.firstName)
        $("#lastname").text(data.lastName)
        $("#email").text(data.email)
        $("#phone").text(data.phone)
        
  getInvoiceDetails: ->
    that = this
    postUrl = $("#invoice-details").attr("data-target")

    $.ajax
      url: postUrl
      type: "POST"
      dataType: "json"
      data:
        JSON.stringify
          invoiceId: that.invoiceId
      contentType: "application/json; charset=utf-8"
      success: (data, textStatus) ->
        # set overdue info
        $("#invoiceoverdue").text(data.overdueCount + " times")

        # set paid info
        $("#paidstatus").text(if data.paid then "Paid" else "Not paid yet")

        # fill line items
        for lineItem in data.lineItems
          $("#lineitems tbody").append "<tr><td>#{lineItem.quantity}</td><td>#{lineItem.product.name}</td><td>#{lineItem.price}</td></tr>"

        # apply day-based discount factor
        $("#days-offset-discount").text(data.daysOffsetDiscount)
        that.applyDayBasedDisountFactor(data.daysOffsetDiscount)

        # add existing payment receipts
        for paymentReceipt in data.paymentReceipts
          that.addPaymentReceiptLine(paymentReceipt.paymentdate, paymentReceipt.value, paymentReceipt.id)   
  
  applyDayBasedDisountFactor: (factor) ->
    $("#invoiceamount-discounted").text(parseFloat($("#invoiceamount").text()) * factor)

  getMaxPaymentReceiptDate: ->
    maxDate = new Date()
    $(".pr-line-date").each (idx, date) ->
      currentDate = new Date($(date).text())
      maxDate = currentDate if maxDate == null or currentDate > maxDate

    maxDate

dunningManager = 
  init: ->
    console.log("Init DunningManager")
    $(".dun").click ->
      dunningUrl = $(this).attr("data-target")

      closestRow = $(this).closest("tr")
      invoiceId = parseInt(closestRow.find(".dunning-invoice-id").text())
      customerId = parseInt(closestRow.find(".dunning-customer-id").text())
      netAmount = parseFloat(closestRow.find(".dunning-net-amount").text())

      $.ajax
        url: dunningUrl
        type: "POST"
        dataType: "json"
        data:
          JSON.stringify
            invoiceId: invoiceId
            customerId: customerId
            netAmount: netAmount
        contentType: "application/json; charset=utf-8"
        success: (data, textStatus) ->
          odCountSpan = closestRow.find(".od-count")
          odCountSpan.text(parseInt(odCountSpan.text()) + 1)

$ ->
  invoiceManager.init()
  dunningManager.init()
  