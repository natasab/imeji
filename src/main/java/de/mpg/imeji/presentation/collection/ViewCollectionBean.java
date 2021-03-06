/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.collection;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectLoader;

/**
 * Bean for the pages "CollectionEntryPage" and "ViewCollection"
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "ViewCollectionBean")
@RequestScoped
public class ViewCollectionBean extends CollectionBean
{
    private List<Person> persons = null;
    private static Logger logger = Logger.getLogger(ViewCollectionBean.class);

    /**
     * Construct a default {@link ViewCollectionBean}
     */
    public ViewCollectionBean()
    {
        super();
    }

    /**
     * Initialize all elements of the page.
     */
    public void init()
    {
        try
        {
            User user = super.sessionBean.getUser();
            String id = getId();
            setCollection(ObjectLoader.loadCollectionLazy(ObjectHelper.getURI(CollectionImeji.class, id), user));
            if (getCollection() != null && getCollection().getId() != null)
            {
                findItems(user, 13);
                loadItems(user);
                countItems(user);
            }
            if (getCollection() != null)
            {
                setProfile(ObjectLoader.loadProfile(getCollection().getProfile(), user));
                setProfileId(ObjectHelper.getId(getProfile().getId()));
                // super.setTab(TabType.COLLECTION);
                persons = new ArrayList<Person>();
                for (Person p : super.getCollection().getMetadata().getPersons())
                {
                    List<Organization> orgs = new ArrayList<Organization>();
                    for (Organization o : p.getOrganizations())
                    {
                        orgs.add(o);
                    }
                    p.setOrganizations(orgs);
                    persons.add(p);
                }
                getCollection().getMetadata().setPersons(persons);
            }
        }
        catch (Exception e)
        {
            BeanHelper.error(e.getMessage());
            logger.error("Error init of collection home page", e);
        }
    }

    public List<Person> getPersons()
    {
        return persons;
    }

    public void setPersons(List<Person> persons)
    {
        this.persons = persons;
    }

    @Override
    protected String getNavigationString()
    {
        return "pretty:collectionInfos";
    }

    public String getPersonString()
    {
        String personString = "";
        for (Person p : getCollection().getMetadata().getPersons())
        {
            if (!"".equalsIgnoreCase(personString))
                personString += " / ";
            personString += p.getFamilyName() + ", " + p.getGivenName() + " ";
        }
        return personString;
    }

    public String getSmallDescription()
    {
        if (this.getCollection() == null || this.getCollection().getMetadata().getDescription() == null)
            return "No Description";
        if (this.getCollection().getMetadata().getDescription().length() > 100)
        {
            return this.getCollection().getMetadata().getDescription().substring(0, 100) + "...";
        }
        else
        {
            return this.getCollection().getMetadata().getDescription();
        }
    }

    /**
     * @return
     */
    public String getFormattedDescription()
    {
        if (this.getCollection() == null || this.getCollection().getMetadata().getDescription() == null)
            return "";
        return this.getCollection().getMetadata().getDescription().replaceAll("\n", "<br/>");
    }

    /**
     * @return
     */
    public String getCitation()
    {
        String title = super.getCollection().getMetadata().getTitle();
        String author = this.getPersonString();
        String url = super.getPageUrl();
        String citation = title + " " + sessionBean.getLabel("from") + " <i>" + author + "</i></br>" + url;
        return citation;
    }
}
