package com.trilobiet.oapen.oapenwebsite.rss;

import java.io.Serializable;
import java.time.LocalDate;

public class RssItemImp implements RssItem, Serializable {
	
	private static final long serialVersionUID = -200073972568322341L;

	private String title, author, description, contents, link;
	private LocalDate date;
	
	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public LocalDate getPublicationDate() {
		return date;
	}

	@Override
	public String getAuthor() {
		return author;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getContents() {
		return contents;
	}

	@Override
	public String getLink() {
		return link;
	}

	public static class Builder {
		
		private String title, link, description, author, contents;
		private LocalDate date;
		
		public Builder(String link) {
			this.link = link;
		}
		
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}
		
		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}

		public Builder setAuthor(String author) {
			this.author = author;
			return this;
		}
		
		public Builder setContents(String contents) {
			this.contents = contents;
			return this;
		}

		public Builder setDate(LocalDate date) {
			this.date = date;
			return this;
		}
		
		public RssItem build() {
			
			RssItemImp item = new RssItemImp();
			item.author = this.author;
			item.contents = this.contents;
			item.date = this.date;
			item.description = this.description;
			item.link = this.link;
			item.title = this.title;
			
			return item;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((link == null) ? 0 : link.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RssItemImp other = (RssItemImp) obj;
		if (link == null) {
			if (other.link != null)
				return false;
		} else if (!link.equals(other.link))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RssItemImp [title=" + title + ", link=" + link + ", date=" + date + "]";
	}
	
}
