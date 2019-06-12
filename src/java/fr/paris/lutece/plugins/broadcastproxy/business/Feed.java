/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.broadcastproxy.business;

import java.util.Map;

/**
 *
 * @author leridons
 */
public class Feed {
    private String _name;
    private String _id;
    private String _description;
    private String _type;
    private boolean _active;
    private Map<String,String> _data;

    /**
     * constructor 
     */
    public Feed( ) {
        this._active = true ;
    }
    /**
     * constructor 
     * 
     * @param _id
     * @param _name
     * @param _type 
     */
    public Feed(String _id, String _name, String _type) {
        this._name = _name;
        this._id = _id;
        this._type = _type;
        this._active = true;
    }

    
    /**
     * get name
     * @return the name
     */            
    public String getName() {
        return _name;
    }

    /**
     * set name
     * @param _name 
     */
    public void setName(String _name) {
        this._name = _name;
    }

    /**
     * get id
     * @return the id
     */
    public String getId() {
        return _id;
    }

    /**
     * set id
     * @param _id 
     */
    public void setId(String _id) {
        this._id = _id;
    }

    /**
     * get description
     * @return the description
     */
    public String getDescription() {
        return _description;
    }

    /**
     * set description
     * @param _description 
     */
    public void setDescription(String _description) {
        this._description = _description;
    }

    /**
     * get type
     * @return the type
     */
    public String getType() {
        return _type;
    }

    /**
     * set type
     * @param _type 
     */
    public void setType(String _type) {
        this._type = _type;
    }

    /**
     * get datas
     * @return the map of datas
     */
    public Map<String, String> getData() {
        return _data;
    }

    /**
     * set datas map
     * 
     * @param _datas
     */
    public void setData(Map<String, String> _data) {
        this._data = _data;
    }
            
    /**
     * get the active state of the feed
     * @return true if active
     */
    public boolean isActive() {
        return _active;
    }

    /**
     * set the active state 
     * @param _active 
     */
    public void setActive(boolean _active) {
        this._active = _active;
    }

}
