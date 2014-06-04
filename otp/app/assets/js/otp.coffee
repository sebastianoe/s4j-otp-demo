addTableRow = (table) ->
    # clone the last row in the table
    $tr = $(table).find("tbody tr:last").clone()

    if $tr.length is 0
      $tr = $(
        '<tr>
              <td>
                <span class="rowIndex">0</span>
                <input type="hidden" value="" class="copyable erasable id">
              </td>
              <td>
                <input type="hidden" class="productId copyable erasable" value="">
                <input type="text" class="product-completer erasable form-control" 
                  value="">
              </td>
              <td>
                <input type="number" value="" class="copyable erasable quantity form-control text-right" 
                  min="0" step="1" value="0">
              </td>
              <td>
                <span class="productUnit erasable"></span>
              </td>
              <td>
                <input type="hidden" class="productPrice copyable erasable" value="">
                <span class="erasable price"></span>
              </td>
              <td>
                <span class="productShippingCost erasable"></span>
              </td>
              <td>
                <input type="text" value="" class="promiseDate copyable erasable datepicker form-control"
                  data-date-format="yyyy-mm-dd">
              </td>
              <td><span class="atpResult erasable success">
                Pass
              </span></td>
              <td class="danger">
                <a href="#" class="deleteRow">Delete</a>
              </td>
            </tr>')
      $(table).find("tbody").append($tr)
  
    # erase marked columns
    $tr.find(".erasable").val("").text("")

    # increase row index
    $tr.find(".rowIndex").text ->
      return parseInt($(this).text()) + 1

    # append the new row to the table
    $(table).find("tbody tr:last").after $tr

    # add autocomplete functionality
    addProductAutocomplete $tr.find(".product-completer")

    # add datepicker functionality
    addDatePicker $tr.find(".datepicker")

    setQuantityChangeHandlerFor $tr

addProductAutocomplete = ($elems) ->
  $elems.typeahead([
      name: "products"
      remote: "/products/json/%QUERY"
      valueKey: "name"
      
  ]).bind "typeahead:selected", (e, datum)->
    $target = $(e.currentTarget)
    $closestRow = $target.closest("tr")
    $closestRow.find(".productId").val datum.id
    $closestRow.find(".productUnit").text datum.unit
    $closestRow.find(".productPrice").val (datum.pprice + datum.defaultMargin).toFixed(2)
    $closestRow.find(".productShippingCost").text datum.shippingCost.toFixed(2)
    updateSoliPrice($target.closest("tr"))
    updateTotalNetValue()
    updateShippingCosts()
    performAtpCheck($closestRow)

addDatePicker = ($elems) ->
  $elems.datepicker().on("changeDate", (ev) ->
    performAtpCheck($(ev.target).closest("tr"))
  )

updateSoliPrice = ($row) ->
  pprice = $row.find(".productPrice").val()
  quantity = $row.find(".quantity").val()
  $row.find(".price").text((pprice * quantity).toFixed(2))

updateTotalNetValue = ->
  prices = $(".price").map(->
    parseFloat($(this).text())).get()
  totalNetValue = prices.reduce (t, s) -> t + s
  $("#totalNetValue").text(totalNetValue.toFixed(2))
  taxRate = 0.19
  $("#tax").text((totalNetValue * taxRate).toFixed(2))
  updateTotal()

updateShippingCosts = ->
  shippingCosts = $(".productShippingCost").map(->
    parseFloat($(this).text())).get()
  totalShippingCosts = shippingCosts.reduce (t, s) -> t + s
  $("#shipping").text(totalShippingCosts.toFixed(2))

updateTotal = ->
  totalNetValue = parseFloat($("#totalNetValue").text())
  discountFactor = parseFloat($("#discount").val()) / 100
  tax = parseFloat($("#tax").text())
  shippingCosts = parseFloat($("#shipping").text())
  totalBeforeDiscount = totalNetValue + tax + shippingCosts
  $("#total").text((totalBeforeDiscount * discountFactor).toFixed(2))

setQuantityChangeHandlerFor = ($row) ->
  $row.find(".quantity").change ->
    updateSoliPrice($(this).closest "tr")
    updateTotalNetValue()
    performAtpCheck($row)

performAtpCheck = ($row) ->
  requestedProductId = parseInt($row.find(".productId").val())
  requestedQuantity = parseInt($row.find(".quantity").val())
  requestedDate = $row.find(".promiseDate").val()

  if requestedProductId != NaN and requestedQuantity != NaN and requestedDate != ""
    requestData =
      requestedProductId: requestedProductId
      requestedDate: requestedDate

    atpResultSpan = $row.find ".atpResult"
    postUrl = $("#atp-url").attr "data-target"
    console.log($row)

    $.ajax
      url: postUrl
      type: "POST"
      dataType: "json"
      data: JSON.stringify(requestData)
      contentType: "application/json; charset=utf-8"
      success: (data, textStatus) ->
        if data.availableAmount >= requestedQuantity
          atpResultSpan.text "Pass"
          atpResultSpan.closest("td").addClass("success").removeClass("danger")
        else
          atpResultSpan.text "Fail. Only #{data.availableAmount} items are available."
          atpResultSpan.closest("td").addClass("danger").removeClass("success")
      error: (data, textStatus) ->
        "Could not get ATP result!"

updateSalesOrderStatus = ($target, successCallback, actionVerb) ->
  postUrl = $target.attr("data-target")
  $.ajax
    url: postUrl
    type: "POST"
    success: successCallback
    error: (data, textStatus) ->
      alert "Could not " + actionVerb + " sales order!"

reject = ($target, successCallback) -> updateSalesOrderStatus($target, successCallback, "reject")
release = ($target, successCallback) -> updateSalesOrderStatus($target, successCallback, "release")

$ ->
  # autocomplete for customer
  $("#customer-completer").typeahead([
      name: "customers"
      remote: "/customers/json/%QUERY"
      valueKey: "name"
      
  ]).bind "typeahead:selected", (e, datum)->
    $("#customerId").val datum.id

  # autocomplete for product
  addProductAutocomplete $(".product-completer")
  addDatePicker $(".datepicker")

  # initial soli prices
  $("#soliTable tbody tr").each ->
    updateSoliPrice $(this)
    setQuantityChangeHandlerFor $(this)

  # add table row
  $("#addRow").click ->
    addTableRow($("#soliTable"))

  # delete table row
  $(".deleteRow").click ->
    $(this).closest("tr").remove()

  # soli save button
  $("#saveSOLIsButton").click ->
    postUrl = $("#save-url").attr("data-target")
    soliData = (for row in $("#soliTable tbody").find("tr")
      {
        id: $(row).find(".id").val(), 
        productId: $(row).find(".productId").val(),
        quantity: $(row).find(".quantity").val(),
        promiseDate: $(row).find(".promiseDate").val(),
        price: $(row).find(".price").text().trim().replace(",", ".")
      })
    
    requestData = {
      soliData: soliData
      discountFactor: parseFloat($("#discount").val()) / 100
    }

    $.ajax
      url: postUrl
      type: "POST"
      data: JSON.stringify(requestData)
      contentType: "application/json; charset=utf-8"
      success: (data, textStatus) ->
        alert data
      error: (data, textStatus) ->
        alert "Could not save products!"
  
  # discount handler
  $("#discount").change ->
    updateTotal()

  # update calculations
  if $("tbody tr").length is not 0
    updateShippingCosts()
    updateTotalNetValue()

  # reject & release
  $(".reject-on-index").click ->
    $that = $(this)
    
    reject($that, (data, textStatus) ->
      $that.closest("tr").find(".status").text("rejected")
      $that.remove()
    )

  $(".reject-on-release").click ->
    $that = $(this)
    
    reject($that, (data, textStatus) ->
      $that.closest("tr").remove()
    )

  $(".release").click ->
    $that = $(this)
    
    reject($that, (data, textStatus) ->
      $that.closest("tr").remove()
    )