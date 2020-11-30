
jQuery(document).ready( function() {
	
	// Check for click events on the navbar burger icon
	$(".navbar-burger").click(function() {
	
		// Toggle the "is-active" class on both the "navbar-burger" and the "navbar-menu"
		$(".navbar-burger").toggleClass("is-active");
		$(".navbar-menu").toggleClass("is-active");
	});
	  
	// make iframes full height
	$('.doab-snippet iframe').on("load", function() {

		var iframe = $(window.top.document).find(".doab-snippet iframe");
		iframe.height(iframe[0].ownerDocument.body.scrollHeight+'px' );
	});	  
	  
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
	
});


