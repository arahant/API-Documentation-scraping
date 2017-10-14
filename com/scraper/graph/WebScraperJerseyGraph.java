package com.scraper.graph;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebScraperJerseyGraph {

	private static final String INDEX_DIR = "/home/arahant/Documents/Main/Career/API docs/";
	private static String baseurl;
	private static String authority;
	private static int c = 0;

	private static LinkedHashMap<String,Vertex> link_node_map;
	private static LinkedHashSet<String> link_set;

	static {
		link_node_map = new LinkedHashMap<String, Vertex>();
		link_set = new LinkedHashSet<String>();
	}

	public static void main(String[] args) throws URISyntaxException, IOException {
		String url = "https://jersey.github.io/documentation/latest/";
		baseurl = url;
		WebScraperJerseyGraph extract = new WebScraperJerseyGraph();

		if(extract.validateURL(url+"index.html")) {
			authority = new URI(url).getAuthority();
			System.err.println("Cawling and forming a web graph...");
			extract.crawl(url+"index.html",1);
			System.err.println("Traversing the web graph, replacing hyperlinks...");
			extract.traverseWebGraph(link_node_map.get(url+"index.html"),0);
		}
	}
	
	private void crawl(String url, int depth) throws IOException {
		System.out.println("Depth "+depth+" "+url);
		try {
			URI uri = new URI(url);
			//this statement ensures that the crawler doesn't digress. 
			//Suppose there is a link to a Java package, this blocks any attempts to visit them.
			if(uri.getAuthority().equals(authority)) {
				//removing fragments from the url to avoid duplicate traversals and downloads
				if(link_set.add(url.split("#")[0])) {
					Document doc = Jsoup.connect(url).get();
					Elements links = doc.getElementsByTag("a");
					if(links.size()==0)
						return;
					else {
						List<String> hyperlinks = new LinkedList<String>();
						for(Element a:links) {
							if(validateURL(baseurl+a.attr("href"))) {
								crawl(baseurl+a.attr("href"),++depth);
								hyperlinks.add(baseurl+a.attr("href"));
							}
						}
						//creating a Vertex instance for this page
						Vertex node = new Vertex();
						node.setId(++c);node.setUrl(url);node.setDomain(uri.getPath());
						node.setHyperlinks(hyperlinks);
						link_node_map.put(url, node);
						downloadDocument(url);
					}
				}
				else
					return;
			}
			else
				return;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		}
	}
	
	private boolean validateURL(String url) {
		try {
			new URI(url);
			if(Jsoup.connect(url).ignoreHttpErrors(true).execute().statusCode()<400)
				return true;
		} catch (URISyntaxException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	private void downloadDocument(String url) {

		try {
			Document doc = Jsoup.connect(url).get();
			FileOutputStream fOut;BufferedOutputStream bOut;
			String path = link_node_map.get(url).getDomain();

			if(new File(path.toString()).mkdirs()) {
				try {
					File file = new File(path+"summary.html");
					fOut = new FileOutputStream(file);
					bOut = new BufferedOutputStream(fOut);
					bOut.write(doc.toString().getBytes());
					bOut.flush();bOut.close();
					System.out.println(path.toString());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else
				System.err.println(path.toString());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

	private void traverseWebGraph(Vertex node, int idFrom) {
		System.out.println("From "+idFrom+" to "+node.getId());
		if(node.isVisited())
			return;
		else {
			node.setVisited(true);
			List<String> hyperlinks = node.getHyperlinks();
			try {
				File file = new File(INDEX_DIR+node.getDomain());
				Document doc = Jsoup.parse(file, "UTF-8");
				String data = new String(doc.toString());
				for(String link:hyperlinks) {
					Vertex target = link_node_map.get(link);
				//using Java Regexp instead of the default String.replace()function reduces complexity (tested!)
					String oldtag = "href=\""+link+"\"";
					String newtag = "href=\""+target.getDomain();
					Pattern p = Pattern.compile(oldtag);
					Matcher m = p.matcher(data);
					data = m.replaceAll(newtag);
					//data = data.replace(oldtag, newtag);
					traverseWebGraph(target, node.getId());
				}
				FileOutputStream fOut = new FileOutputStream(file);
				BufferedOutputStream bOut = new BufferedOutputStream(fOut);
				bOut.write(data.getBytes());
				bOut.flush();bOut.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
