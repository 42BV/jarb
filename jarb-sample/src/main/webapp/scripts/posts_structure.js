$.getJSON('http://localhost:8080/jarb-sample/posts/structure.json', function(json) {
  /*
   * TODO: Use constraint information to enhance HTML form with validation and length attribute
   * 
   * {
   * 	"beanConstraintMetadata": {
   * 		"beanType":"org.jarb.sample.domain.Post",
   * 		"propertiesMetadata":[
   * 			{"required":false,"propertyName":"message","propertyType":"java.lang.String","maximumLength":16777216,"fractionLength":null,"radix":null,"minimumLength":null},
   * 			{"required":false,"propertyName":"id","propertyType":"java.lang.Long","maximumLength":64,"fractionLength":0,"radix":2,"minimumLength":null},
   * 			{"required":true,"propertyName":"author","propertyType":"java.lang.String","maximumLength":255,"fractionLength":null,"radix":null,"minimumLength":null},
   * 			{"required":true,"propertyName":"title","propertyType":"java.lang.String","maximumLength":255,"fractionLength":null,"radix":null,"minimumLength":null},
   * 			{"required":false,"propertyName":"class","propertyType":"java.lang.Class","maximumLength":null,"fractionLength":null,"radix":null,"minimumLength":null},
   * 			{"required":true,"propertyName":"postedOn","propertyType":"java.util.Date","maximumLength":26,"fractionLength":null,"radix":null,"minimumLength":null}
   * 		]
   * 	}
   * }
   */
  
  // Show the bean type
  $('body').append('<p>' + json.beanConstraintMetadata.beanType + '</p>')
  
  // Show all property names
  var items = [];
  $.each(json.beanConstraintMetadata.propertiesMetadata, function(index, propertyMetadata) {
    items.push('<li>' + propertyMetadata.propertyName + '</li>');
  });
  $('<ul/>', {
    'class': 'properties',
    html: items.join('')
  }).appendTo('body');
  
});
