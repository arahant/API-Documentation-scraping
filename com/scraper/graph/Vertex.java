/*
Vertex or Node is the custom data type I used to create a graph structure.
In the web structure it is basically a web page,containing its URL, and a list of all the hyperlinks.
These hyperlinks are what I used to create the links in the graph structure.

The Integer id is the count of nodes in the graph.
The String url is the url of this Node(page).
The String domain is the path of this page (obtained using java.net.URI).
The List hyperlinks are a list of links to other peer, parent or child Nodes(pages).

The boolean visited is very handy while traversing the graph, helping in avoiding infinite looping.
The Integer rank is the rank of the Node (url/page), which increments everytime this page is referred to from another Node/vertex.
The String category is self-explanatory, which stores the semantic value about the page.
*/

package com.scraper.graph;

import java.util.List;

public class Vertex {
	
	private int id, rank;
	private boolean visited;
	private String url, domain, category;
	private List<String> hyperlinks;
	
	public void setId(int id) { this.id = id; }
	public void setUrl(String url) { this.url = url; }
	public void setDomain(String domain) { this.domain = domain; }
	public void setVisited(boolean visited) { this.visited = visited; }
	public void upRank() { this.rank++; }
	public void setCategory(String category) { this.category = category; }
	public void setHyperlinks(List<String> hyperlinks) { this.hyperlinks = hyperlinks; }
	
	public int getId() { return id; }
	public String getUrl() { return url; }
	public String getDomain() {	return domain; }
	public boolean isVisited() { return visited; }
	public int getRank() { return rank; }
	public String getCategory() { return category; }
	public List<String> getHyperlinks() { return hyperlinks; }

}
