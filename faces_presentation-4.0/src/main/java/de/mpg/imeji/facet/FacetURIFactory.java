package de.mpg.imeji.facet;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.filter.Filter;
import de.mpg.imeji.search.URLQueryTransformer;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.SearchCriterion;

public class FacetURIFactory
{
	private SessionBean sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
	private List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
	
	public FacetURIFactory(List<SearchCriterion> scList) 
	{
		this.scList = scList;
	}
	
	public URI createFacetURI(String baseURI, SearchCriterion sc) throws UnsupportedEncodingException
	{
		if(sc != null) scList.add(sc);
		String uri = baseURI + getCommonURI();
		scList.remove(sc);
		return URI.create(uri);
	}
	
	private String getCommonURI() throws UnsupportedEncodingException
	{
		List<Filter> filters = sb.getFilters();
        String commonURI ="";
        int i=0;
        for (Filter f : filters)
        {
        	if (!"".equals(f.getQuery()))
        	{
        		commonURI += "&f" + i + "=" + URLEncoder.encode(f.getQuery(), "UTF-8");
        		i++;
        	}
        }
        commonURI +=  URLEncoder.encode(URLQueryTransformer.transform2URL(scList), "UTF-8");
        return commonURI;
	}
	
}
