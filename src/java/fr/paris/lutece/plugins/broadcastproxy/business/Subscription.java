/*
 * Copyright (c) 2002-2019, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.broadcastproxy.business;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author leridons
 */
public class Subscription
{

    private String _id;
    private String _userId;
    private String _userName;
    private String _type;
    private boolean _isActive;
    private Map<String, String> _data;

    /**
     * get id
     * 
     * @return the id
     */
    public String getId( )
    {
        return _id;
    }

    /**
     * set id
     * 
     * @param strId
     */
    public void setId( String strId )
    {
        this._id = strId;
    }

    /**
     * get user id
     * 
     * @return the id
     */
    public String getUserId( )
    {
        return _userId;
    }

    /**
     * set user id
     * 
     * @param userId
     */
    public void setUserId( String userId )
    {
        this._userId = userId;
    }

    /**
     * get user name
     * 
     * @return the name
     */
    public String getUserName( )
    {
        return _userName;
    }

    /**
     * set user name
     * 
     * @param userName
     */
    public void setUserName( String userName )
    {
        this._userName = userName;
    }

    /**
     * get type
     * 
     * @return the type
     */
    public String getType( )
    {
        return _type;
    }

    /**
     * set type
     * 
     * @param type
     */
    public void setType( String type )
    {
        this._type = type;
    }

    /**
     * test if active
     * 
     * @return true if active
     */
    public boolean isActive( )
    {
        return _isActive;
    }

    /**
     * set active state
     * 
     * @param isActive
     */
    public void setActive( boolean isActive )
    {
        this._isActive = isActive;
    }

    /**
     * returns specific additionnal data
     * 
     * @return data
     */
    public Map<String, String> getData( )
    {
        return _data;
    }

    /**
     * set specific datas for the subscription
     * 
     * @param data
     */
    public void setData( Map<String, String> data )
    {
        this._data = data;
    }

    /**
     * add a data
     * 
     * @param strName
     * @param strValue
     */
    public void addDataItem( String strName, String strValue )
    {
        if ( _data == null )
            _data = new HashMap<>( );
        this._data.put( strName, strValue );
    }
}
