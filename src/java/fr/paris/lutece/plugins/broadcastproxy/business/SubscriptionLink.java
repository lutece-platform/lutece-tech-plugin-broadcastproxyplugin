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
package fr.paris.lutece.plugins.broadcastproxy.business;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
/**
 * This is the business class for the object SubscriptionLink
 */ 
public class SubscriptionLink implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations 
    private int _nId;
    
    @NotEmpty( message = "#i18n{broadcastproxy.validation.subscriptionlink.Label.notEmpty}" )
    @Size( max = 255 , message = "#i18n{broadcastproxy.validation.subscriptionlink.Label.size}" ) 
    private String _strLabel;
    
    private String _strPictogramme;
    
    @NotEmpty( message = "#i18n{broadcastproxy.validation.subscriptionlink.Description.notEmpty}" )
    private String _strDescription;
    
    private String _strFrequency;
    
    private String _strGroup;
    
    private int _nGroupId;
    
    private int _nSubscriptionId;
    
    private int _nInterestId;
    
    private boolean _nEnabled;
    
    /**
     * Returns the Id
     * @return The Id
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * Sets the Id
     * @param nId The Id
     */ 
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     * Returns the Label
     * @return The Label
     */
    public String getLabel( )
    {
        return _strLabel;
    }

    /**
     * Sets the Label
     * @param strLabel The Label
     */ 
    public void setLabel( String strLabel )
    {
        _strLabel = strLabel;
    }
    
    
    /**
     * Returns the Pictogramme
     * @return The Pictogramme
     */
    public String getPictogramme( )
    {
        return _strPictogramme;
    }

    /**
     * Sets the Pictogramme
     * @param strPictogramme The Pictogramme
     */ 
    public void setPictogramme( String strPictogramme )
    {
        _strPictogramme = strPictogramme;
    }
    
    
    /**
     * Returns the Description
     * @return The Description
     */
    public String getDescription( )
    {
        return _strDescription;
    }

    /**
     * Sets the Description
     * @param strDescription The Description
     */ 
    public void setDescription( String strDescription )
    {
        _strDescription = strDescription;
    }

    /**
     * @return the _strFrequency
     */
    public String getFrequency( )
    {
        return _strFrequency;
    }

    /**
     * @param strFrequency the _strFrequency to set
     */
    public void setFrequency( String strFrequency )
    {
        this._strFrequency = strFrequency;
    }

    /**
     * Returns the Group
     * @return The Group
     */
    public String getGroup( )
    {
        return _strGroup;
    }

    /**
     * Sets the Group
     * @param nCategoryId The Group
     */ 
    public void setGroup( String strGroup )
    {
        _strGroup = strGroup;
    }
    
    /**
     * @return the _nGroupId
     */
    public int getGroupId( )
    {
        return _nGroupId;
    }

    /**
     * @param _nGroupId the _nGroupId to set
     */
    public void setGroupId( int _nGroupId )
    {
        this._nGroupId = _nGroupId;
    }

    /**
     * Returns the SubscriptionId
     * @return The SubscriptionId
     */
    public int getSubscriptionId( )
    {
        return _nSubscriptionId;
    }

    /**
     * Sets the SubscriptionId
     * @param nSubscriptionId The SubscriptionId
     */ 
    public void setSubscriptionId( int nSubscriptionId )
    {
        _nSubscriptionId = nSubscriptionId;
    }

    
    /**
     * @return the _nInterestId
     */
    public int getInterestId( )
    {
        return _nInterestId;
    }

    /**
     * @param nInterestId the _nInterestId to set
     */
    public void setInterestId( int nInterestId )
    {
        this._nInterestId = nInterestId;
    }

    /**
     * @return the _nEnabled
     */
    public boolean isEnabled( )
    {
        return _nEnabled;
    }

    /**
     * @param nEnabled the _nEnabled to set
     */
    public void setEnabled( boolean nEnabled )
    {
        this._nEnabled = nEnabled;
    }
    
}
