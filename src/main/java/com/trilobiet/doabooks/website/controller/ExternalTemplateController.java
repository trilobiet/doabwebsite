package com.trilobiet.doabooks.website.controller;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Request the template and rewrite to be used as an external template.
 * Header and footer can be requested separately.
 * <p>
 * All links and resource paths are resolved.
 * <p>
 * Usage:<br>
 * https://www.doabooks.org/doabwebsite/layout/en<br>
 * https://www.doabooks.org/doabwebsite/layout/en/header<br>
 * https://www.doabooks.org/doabwebsite/layout/en/footer<br>
 * https://www.doabooks.org/doabwebsite/layout/fr<br>
 * https://www.doabooks.org/doabwebsite/layout/fr/header<br>
 * https://www.doabooks.org/doabwebsite/layout/fr/footer<br>
 * etc.<br>
 * 
 * @author acdhirr
 *
 */
@Controller
public class ExternalTemplateController extends BaseController {
	
	@RequestMapping(
		value = {
			"/layout/{language}/{selection:html|header|footer}",
			"/layout/{language}/{selection:html|header|footer}/{domain}"
		}, 
		produces = MediaType.TEXT_HTML_VALUE
	)
	@ResponseBody // bypass thymeleaf - this method returns html itself
	public String showLayout(
		HttpServletRequest request,
		@PathVariable(required=true, name="selection" ) String selection,
		@PathVariable(required=false, name="domain" ) Optional<String> domain
	) throws IOException {
		
		String html = "";
		String baseUrl = "https://" + domain.orElse("doabooks.org");
		
		StringBuilder url = new StringBuilder(request.getScheme());
		url.append("://").append(request.getServerName());
		if (request.getLocalPort() != 80) url.append(":").append(request.getLocalPort());
		url.append(request.getContextPath());
		url.append("/").append(language);
		
		Connection con = Jsoup.connect(url.toString());
		con.timeout( 20000 );
		con.userAgent("Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:46.0) Gecko/20100101 Firefox/46.0");
		
		OutputSettings os = new OutputSettings();
		os.prettyPrint(true);
		os.indentAmount(3);
		os.outline(true);
		
		Document doc = con.get();
		doc.setBaseUri(baseUrl);
		doc.outputSettings(os);
		parseRelativePaths(doc);
		
		doc.select("[rel='stylesheet']").not("[href*='fontawesome']").remove();
		doc.getElementsByClass("doab-home-hero").remove();
		doc.getElementsByClass("doab-footermsg").remove();
		doc.select(".doab-main *").remove();

		// remove scripts
		doc.select("script").not("[src*='menu']").remove();
		
		// remove submenus
		doc.select(".navbar-dropdown").remove();
		doc.select(".navbar-item").removeClass("navbar-link");
		
		removeComments(doc.root());
		
		// attach minimal css only header footer
		String newCss = "<link rel=\"stylesheet\" href=\"" + baseUrl + "/static-assets/css/hdrftr.css?v=1.01\">"; 
		doc.select("head").append(newCss);
		
		if (selection != null) html = doc.select(selection).outerHtml();
		else html = doc.html();
		
		return html;
	}
	
	
	/**
	 * In the remote document resolve relative urls in href and src attributes.
	 * 
	 * @param doc JSoup HTML Document to process.
	 */
	private void parseRelativePaths( Document doc ) {
		
		Elements links = doc.select( "a[href], link[href]" );
	    for (Element el : links){
	        String attr = el.attr( "abs:href" ).replaceFirst(";jsessionid=[A-Z0-9]*", "");
	        el.attr( "href", attr );
	    }

		Elements srcs = doc.select( "script[src], img[src]" );
	    for (Element el : srcs){
	        String attr = el.attr( "abs:src" ).replaceFirst(";jsessionid=[A-Z0-9]*", "");
	        el.attr( "src", attr );
	    }

	    Elements opts = doc.select( "option[value]" );
	    for (Element el : opts){
	        String attr = el.attr( "abs:value" ).replaceFirst(";jsessionid=[A-Z0-9]*", "");
	        el.attr( "value", attr );
	    }
	}	
	
	private void removeComments(Node node) {
		for (int i = 0; i < node.childNodeSize();) {
			Node child = node.childNode(i);
			if (child.nodeName().equals("#comment"))
				child.remove();
			else {
				removeComments(child);
				i++;
			}
		}
	}	
	
}
