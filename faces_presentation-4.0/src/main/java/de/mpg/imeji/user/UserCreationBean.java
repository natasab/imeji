package de.mpg.imeji.user;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import thewebsemantic.NotFoundException;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.user.util.EmailClient;
import de.mpg.imeji.user.util.PasswordGenerator;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.Scripts;
import de.mpg.jena.controller.AlbumController;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.UserController;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.Album;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.User;

public class UserCreationBean
{
    private User user;
    private SessionBean sb;
    
    private Logger logger = Logger.getLogger(UserCreationBean.class);
    
    public UserCreationBean()
    {
    	sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
    	this.setUser(new User());
    }
    
    public String create() throws Exception
    {
        UserController uc = new UserController(sb.getUser());
        String regexEmailMatch ="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
        
        if (!user.getEmail().matches(regexEmailMatch))
        {
            BeanHelper.error(sb.getMessage("error_user_email_not_valid"));
        }
        else
        {
            try
            {
                if (uc.retrieve(user.getEmail()) != null)
                {
                	BeanHelper.error(sb.getMessage("error_user_already_exists"));
                }
            }
            catch (NotFoundException e) 
            {
            	PasswordGenerator generator = new PasswordGenerator();
            	String password = generator.generatePassword();
            	
            	user.setEncryptedPassword(UserController.convertToMD5(password));
                uc.create(user);
                
                EmailClient emailClient = new EmailClient();
                emailClient.sendMailForPassword(user.getEmail(), password, user.getName(), true);
                
                logger.info("USER email created: " + user.getEmail());
                BeanHelper.info(sb.getMessage("success_user_create"));
			}
            catch (Exception e) 
            {
            	BeanHelper.error(sb.getMessage("error") + ": " + e);
			}
        }
        return "pretty:";
    }
    
    public List<User> getAllUsers() throws IllegalArgumentException, IllegalAccessException
    {
    	List<User> users = new ArrayList<User>();
    	UserController uc = new UserController(user);
    	users.addAll(uc.retrieveAll());
    	for(User u : users)
    	{
    		u = (User) ObjectHelper.castAllHashSetToList(u);
    	}
    	return users;
    }
    
    public List<CollectionImeji> getAllcollections()
    {
    	CollectionController cc = new CollectionController(sb.getUser());
    	return (List<CollectionImeji>) cc.retrieveAll();
    }
    
    public List<Album> getAllAlbums()
    {
    	AlbumController ac = new AlbumController(sb.getUser());
    	return (List<Album>) ac.retrieveAll();
    }
    
    public int getAllAlbumsSize()
    {
    	return this.getAllAlbums().size();
    }
    
    public int getAllCollectionsSize()
    {
    	return getAllcollections().size();
    }
    
    public int getAllImagesSize()
    {
    	ImageController ic = new ImageController(sb.getUser());
    	return ic.allImagesSize();
    }
    
    public int getAllUsersSize() 
    {
    	try { return this.getAllUsers().size(); }
		catch (Exception e) { return 0; }
    }
    
    // For testing purpose
    public String copyDataFromCoreToCore() throws IOException, URISyntaxException, Exception
    {
    	Scripts scripts = new Scripts();
    	
    	scripts.copyDataFromCoreToCore(sb.getUser());
    	
    	return "";
    }
    
    public void setUser(User user)
    {
        this.user = user;
    }

    public User getUser()
    {
        return user;
    }
    
    public boolean isSysAdmin()
    {
    	return sb.isAdmin();
    }
}
