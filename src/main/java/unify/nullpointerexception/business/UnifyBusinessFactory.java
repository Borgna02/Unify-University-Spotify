package unify.nullpointerexception.business;

import unify.nullpointerexception.business.implementation.file.FileUnifyBusinessFactory;
// import unify.nullpointerexception.business.implementation.ram.RAMUnifyBusinessFactory;

public abstract class UnifyBusinessFactory {

	// private static UnifyBusinessFactory factory = new RAMUnifyBusinessFactory();
	private static UnifyBusinessFactory factory = new FileUnifyBusinessFactory();

	public static UnifyBusinessFactory getInstance() {
		return factory;
	}

	public abstract UserService getUserService();

	public abstract MusicService getMusicService();

	public abstract ArtistsService getArtistsService();

	public abstract AlbumService getAlbumService();

	public abstract PlaylistService getPlaylistService();

	public abstract GenreService getGenreService();

	public abstract ArtistPicturesService getArtistPicturesService();
}
