<!DOCTYPE html>
<html lang="en">
  <head>
  	<title>Black Plum Apparel Searched Terms</title>
  	<meta charset="utf-8" />
  </head>
  <body style="font-size: 16px;">
        
      <section style="border: solid 1px #b5b5b5;
        border-radius: 4px; 
		padding: 0em 0.7em 0.5em 0.7em;
		margin: 1em 0em 0.8em 0em;">
        
        <h2 style="font-size: 1rem; margin-top: 1.5rem;">Searched Terms ${now}</h2>
        
        <table style="width:100%">
		  <tr>
		    <th>Term</th>
		    <th>Count</th> 
		    <th>Date</th>
		  </tr>
        
          <#list searchTerms as searchTerm>
			  <tr>
			    <td>${searchTerm.term}</td>
			    <td>${searchTerm.count}</td> 
			    <td>${searchTerm.searchDate}</td>
			  </tr>
          </#list>
        </table>
         
     </section>

  </body>
</html>