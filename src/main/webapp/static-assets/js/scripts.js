
jQuery(document).ready( function() {
	
	// make iframes full height OBSOLETE: set iframe height attribute in snippet code
	/*$('.doab-snippet iframe').on("load", function() {

		var iframe = $(window.top.document).find(".doab-snippet iframe");
		iframe.height(iframe[0].ownerDocument.body.scrollHeight+'px' );
	});*/	  
	  
	$(".ajaxloader").each( function() {
		
		var src = $(this).attr("data-src");
		$(this).html(" <i class='fa fa-spinner fa-pulse fa-fw'></i><i>&nbsp;connecting to library...</i>" );
		$(this).load( src, function() {
			// whatever
		});				
	});	  

	$(".carouselloader").each( function() {
		
		var src = $(this).attr("data-src");
		$(this).html(" <i class='fa fa-spinner fa-pulse fa-fw'></i><i>&nbsp;connecting to library...</i>" );
		$(this).load( src, function() {
			bulmaCarousel.attach('#slider', {
				slidesToScroll: 1,
				slidesToShow: 7,
				pagination: false
			});
		});				
	});	  
	
	// wrap content tables in a horizontal scrolling div
	$(".content table").wrap("<div class='doab-table-wrapper'></div>");
	$("<div class='doab-table-swipe'>swipe to view table</div>").insertBefore($(".doab-table-wrapper"));
	
	// faqs
	$(".doab-faq .doab-article h3").click( function(){
	
		$(this).toggleClass("expanded")
	
	});
	
	
	
});


