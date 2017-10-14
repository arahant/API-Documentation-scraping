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
