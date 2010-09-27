package de.mpg.imeji.album;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.imeji.beans.BasePaginatorListSessionBean;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.facet.FacetsBean;
import de.mpg.imeji.image.ImageBean;
import de.mpg.imeji.image.ImagesBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.vo.util.ImejiFactory;
import de.mpg.jena.controller.AlbumController;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.Album;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.Statement;

public class AlbumImagesBean extends ImagesBean
{
    private int totalNumberOfRecords;
    private String id = null;
    private AlbumBean album;
    private URI uri;
    private SessionBean sb;
    private CollectionImeji collection;

    public AlbumImagesBean()
    {
        super();
        this.sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        
    }

    public void init()
    {
        
            AlbumController ac = new AlbumController(sb.getUser());
            this.setAlbum(new AlbumBean(ac.retrieve(id)));
        
    }
    
    @Override
    public String getNavigationString()
    {
        return "pretty:";
    }

    @Override
    public int getTotalNumberOfRecords()
    {
        return totalNumberOfRecords;
    }

    @Override
    public List<ImageBean> retrieveList(int offset, int limit)
    {
        
        ImageController controller = new ImageController(sb.getUser());
        
        uri = ObjectHelper.getURI(Album.class, id);
        
        Collection<Image> images = new ArrayList<Image>();
        try
        {
            totalNumberOfRecords = controller.searchImageInContainer(uri, null, null, -1, offset).size();
            images = controller.searchImageInContainer(uri, null, null, -1, offset);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        super.setFacets(new FacetsBean((List<Image>)images));
        return ImejiFactory.imageListToBeanList(images);
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

  

    public void setCollection(CollectionImeji collection)
    {
        this.collection = collection;
    }

    public CollectionImeji getCollection()
    {
        return collection;
    }

    public void setAlbum(AlbumBean album)
    {
        this.album = album;
    }

    public AlbumBean getAlbum()
    {
        return album;
    }


}