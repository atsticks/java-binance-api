/*
 * MIT License
 *
 * Copyright (c) 2017 Web Cerebrium
 * Copyright (c) 2021 Anatole Tresch
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.webcerebrium.binance.api;

import com.google.common.base.Strings;

import java.util.Properties;

public class BinanceConfig {

    /**
     * properties that are loaded from local resource file
     */
    private Properties prop = null;

    public BinanceConfig() {
        this.loadProperties();
    }

    /**
     *  Loading available properties from local resource file
     */
    protected void loadProperties() {
        try {
            prop = new Properties();
            prop.load(this.getClass().getClassLoader().getResourceAsStream("application.properties"));
        } catch (Exception e) {
            // it is fine not to have that resource file.
            // ignoring any error here
        }
    }

    /**
     * Getting variable from one of the multiple sources available
     * @param key variable name
     * @return string result
     */
    public String getVariable(String key) {
        // checking VM options for properties
        String sysPropertyValue = System.getProperty(key);
        if (!Strings.isNullOrEmpty(sysPropertyValue)) return sysPropertyValue;

        // checking enviroment variables for properties
        String envPropertyValue = System.getenv(key);
        if (!Strings.isNullOrEmpty(envPropertyValue)) return envPropertyValue;

        // checking resource file for property
        if (prop != null) {
            String property = prop.getProperty(key);
            if (!Strings.isNullOrEmpty(property)) return property;
        }
        return "";
    }

}
