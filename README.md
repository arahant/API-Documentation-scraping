# API-Documentation-scraping
Java and Android API docs scraping through HTML parsing.

Using Jsoup library (and HTML parsing), crawling through the documentation pages of Android and (Oracle) Java, and scraping relavent packages', classes' and interfaces' pages, upto a max depth level 2, starting from the (root) domain page.

The crawling type used here is a tree-based or hierarchical crawling in a recursive manner, for each link, and obtaining a hierarchy of links.

This hierarchy is further converted into a web graph.
