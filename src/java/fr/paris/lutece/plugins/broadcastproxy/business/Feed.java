/*
 * Copyright (c) 2002-2023, City of Paris
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

import java.util.Map;

/**
 *
 * @author leridons
 */
public class Feed
{
    private String _name;
    private String _id;
    private String _description;
    private String _type;
    private boolean _active;
    private Map<String, String> _data;

    /**
     * constructor
     */
    public Feed( )
    {
        this._active = true;
    }

    /**
     * constructor
     * 
     * @param _id
     * @param _name
     * @param _type
     */
    public Feed( String _id, String _name, String _type )
    {
        this._name = _name;
        this._id = _id;
        this._type = _type;
        this._active = true;
    }

    /**
     * get name
     * 
     * @return the name
     */
    public String getName( )
    {
        return _name;
    }

    /**
     * set name
     * 
     * @param _name
     */
    public void setName( String _name )
    {
        this._name = _name;
    }

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
     * @param _id
     */
    public void setId( String _id )
    {
        this._id = _id;
    }

    /**
     * get description
     * 
     * @return the description
     */
    public String getDescription( )
    {
        return _description;
    }

    /**
     * set description
     * 
     * @param _description
     */
    public void setDescription( String _description )
    {
        this._description = _description;
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
     * @param _type
     */
    public void setType( String _type )
    {
        this._type = _type;
    }

    /**
     * get datas
     * 
     * @return the map of datas
     */
    public Map<String, String> getData( )
    {
        return _data;
    }

    /**
     * set datas map
     * 
     * @param _datas
     */
    public void setData( Map<String, String> _data )
    {
        this._data = _data;
    }

    /**
     * get the active state of the feed
     * 
     * @return true if active
     */
    public boolean isActive( )
    {
        return _active;
    }

    /**
     * set the active state
     * 
     * @param _active
     */
    public void setActive( boolean _active )
    {
        this._active = _active;
    }

}
