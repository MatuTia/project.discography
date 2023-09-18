package project.discography.model;

import java.util.Objects;

public class Album {

	private final String id;
	private String title;
	private final String musician;

	public Album(String id, String title, String musician) {
		this.id = id;
		this.title = title;
		this.musician = musician;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getMusician() {
		return musician;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "Album [id=" + id + ", title=" + title + ", musician=" + musician + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, musician, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Album other = (Album) obj;
		return Objects.equals(id, other.id) && Objects.equals(musician, other.musician)
				&& Objects.equals(title, other.title);
	}

}
