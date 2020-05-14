<!DOCTYPE html>
<html lang="en">
  <head>
  	<title>Black Plum Apparel Purchase Confirmation</title>
  	<meta charset="utf-8" />
  </head>
  <body style="font-size: 16px;">
  
    <a href="https://blackplumapparel.com">
      <img src="https://storage.googleapis.com/us.artifacts.glass-icon-219701.appspot.com/icons/bpa_logo.png" alt="Black Plum Apparel. Selling t-shirts with original designs." style="height: 7em;" />
    </a>
  
    <article>
      <h1 style="font-family: 'Exo 2', sans-serif;
		  font-size: 1.5em;
		  padding: 0em;
		  margin: 0.6em 0em 0.3em 0em;
		  vertical-align: bottom;">Order Details</h1>
      
      <p>Thank you for your purchase from <span style="font-weight: bold; color: #555568;">Black Plum Apparel</span>!</p>
      
      <section style="border: solid 1px #b5b5b5;
        border-radius: 4px;
        padding: 0.5em 0.7em;
        margin: 1em 0em 0em 0em;">
  		
        <div style="padding: 0.2em 0em;">
          <span>Transaction #:</span>
          <a href="https://blackplumapparel.com/order/${transactionInfo.id}">${transactionInfo.id}</a>
        </div>
        
        <div style="padding: 0.2em 0em;">
          <span>Order Date:</span>
          ${orderDate} (CST)
        </div>
        
        <p style="font-weight: bold;">Check your latest order status <a href="https://blackplumapparel.com/order/${transactionInfo.id}">here</a>.</p>
      </section>
        
      <section style="border: solid 1px #b5b5b5;
        border-radius: 4px; 
		padding: 0em 0.7em 0.5em 0.7em;
		margin: 1em 0em 0.8em 0em;">
        
        <h2 style="font-size: 1rem; margin-top: 1.5rem;">Items Summary</h2>
        
        <section aria-label="Selected Items" style="margin-bottom: 1.3em;">
        
          <#list transactionInfo.order.orderItems as item>
          
	          <div style="padding-bottom: 0.5em;">
	          
	            <picture>
	          	  <img src="${item.imageUrl}" alt="${item.imageAltDescription}" style="border: solid 1px #b5b5b5; border-radius: 4px; height: 6em;">
	           </picture>
	         
	           <div style="display: inline-block; vertical-align: top;">
	             <div style="padding: 0em 0em 0.1em 0.5em;"><a href="https://blackplumapparel.com/details/${item.id}">${item.name}</a></div>
	             <div style="padding: 0em 0em 0.1em 0.5em;">Style: ${item.size} / ${item.color}</div>
	             <div style="padding: 0em 0em 0.1em 0.5em;">Qty: ${item.quantity}</div>
	             <div style="padding: 0em 0em 0.1em 0.5em;">Price: ${item.price?string.currency}</div>
	           </div>
	         </div>
          </#list>
         
       </section>
       
       <section aria-label="Shipping Address" style="font-size: 1rem; margin-top: 1.5rem;">
         <h2 style="font-size: 1rem; margin-top: 1.5rem;">Shipping Address</h2>
         
         <div>
           <div style="padding-bottom: 0.2em;">${transactionInfo.order.shippingAddress.firstName} ${transactionInfo.order.shippingAddress.lastName}</div>
           <div style="padding-bottom: 0.2em;">${transactionInfo.order.shippingAddress.street}</div>
           <div style="padding-bottom: 0.2em;">${transactionInfo.order.shippingAddress.city}, ${transactionInfo.order.shippingAddress.state} ${transactionInfo.order.shippingAddress.zip}</div>
         </div>
       </section>
         
       <section aria-label="Shipping Method" style="margin: 1.4em 0em;">
         <h2 style="font-size: 1rem; margin-top: 1.5rem;">Shipping Method</h2>
         
         <div>${transactionInfo.order.shippingMethodDescription}</div>
       </section>
     </section>
     
     <section style="padding: 0.7em;
       margin: 1em 0em 0em 0em; 
       border: solid 1px #b5b5b5; 
       border-radius: 4px; 
       padding-top: 0rem;">
       
       <h2 style="font-size: 1rem; margin-top: 1.5rem;">Transaction Totals</h2>
       
       <div style="padding-bottom: 0.2em;">
         <span style="display: inline-block; padding-right: 0.7em; width: 6em;">Subtotal:</span>
         <span>${transactionInfo.totals.subtotal?string.currency}</span>
       </div>
       
       <div style="padding-bottom: 0.2em;">
         <span style="display: inline-block; padding-right: 0.7em; width: 6em;">Shipping:</span>
         <span>${transactionInfo.totals.shipping?string.currency}</span>
       </div>
       
       <div style="padding-bottom: 0.2em;">
         <span style="display: inline-block; padding-right: 0.7em; width: 6em;">Tax:</span>
         <span>${transactionInfo.totals.tax?string.currency}</span>
       </div>
       
       <hr>
       
       <div style="padding: 0.4em 0em;">
         <span style="display: inline-block; padding-right: 0.7em; width: 6em;">Total:</span>
         <span>${transactionInfo.totals.total?string.currency}</span>
       </div>
     </section>
   </article>
   
   <div style="margin: 0.6em 2.8em 0.2em 0.2em; display: inline-block;">Please review our <a href="http://blackplumapparel.com/info/returns">Return/Refund Policy</a></div>
   <div style="margin: 0.2em 2.8em 0.2em 0.2em; display: inline-block;">Have a question about this order? <a href="mailto:support@blackplumapparel.com">Contact Us</a></div>
   <div style="margin: 0.2em 2.8em 0.2em 0.2em; display: inline-block;">Let's stay connected on <a href="https://twitter.com/ApparelPlum/">Twitter</a> and <a href="https://www.facebook.com/blackplumapparel/">Facebook</a></div>

  </body>
</html>