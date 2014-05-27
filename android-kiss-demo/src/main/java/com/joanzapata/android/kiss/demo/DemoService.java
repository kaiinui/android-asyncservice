/**
 * Copyright 2014 Joan Zapata
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.joanzapata.android.kiss.demo;

import android.content.Context;
import android.widget.Toast;
import com.joanzapata.android.kiss.api.annotation.ApplicationContext;
import com.joanzapata.android.kiss.api.annotation.Cached;
import com.joanzapata.android.kiss.api.annotation.ErrorManagement;
import com.joanzapata.android.kiss.api.annotation.Init;
import com.joanzapata.android.kiss.api.annotation.KissService;
import com.joanzapata.android.kiss.api.annotation.Mapping;
import com.joanzapata.android.kiss.api.annotation.Ui;
import com.joanzapata.android.kiss.demo.event.UserEvent;

import static android.text.format.DateUtils.DAY_IN_MILLIS;
import static com.joanzapata.android.kiss.api.annotation.Cached.Usage.CACHE_ONLY;

@KissService
public class DemoService {

    /*
        If you need a context you can inject
        the application context here.
    */
    @ApplicationContext
    protected Context applicationContext;

    @Init
    static void initStatic() {
        // Executed once for all services
    }

    @Init
    void init() {
        // Executed once for this service
    }

    /*
        By default, methods are executed in a background thread.
        No caching is involved.
    */
    @ErrorManagement(
            @Mapping(on = 200, send = DummyErrorMessage.class)
    )
    public UserEvent getUserAsync(Long id) {
        return new UserEvent(id, "Joan", 25);
    }

    /*
        If you use @Cached, the result is cached before being sent to the receiver(s).
        On next call, receivers will immediately receive the previous cached event, then
        this method is called. Its new result replaces the previous cache, then is sent
        to the receivers.

        The default cache key is <class_name>.<method_name>(<arg1.toString>, <arg2.toString>, ...)
    */
    @Cached
    public UserEvent getUserAsyncWithCache(Long id) {
        sleep();
        displayMessage("This is a toast displayed from the DemoService.");
        return new UserEvent(id, "Joan", 25);
    }

    @Ui
    protected void displayMessage(String message) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show();
    }

    /*
        Same thing as above, except that this time, if a cached value
        is found, the cache is sent and this method is not called at all.
        This cache expires after one day. Passed this duration, on next
        call the cache is ignored and this method is called again.
     */
    @Cached(usage = CACHE_ONLY, validity = DAY_IN_MILLIS)
    public UserEvent getUserAsyncWithPersistingCache(Long id) {
        sleep();
        return new UserEvent(id, "Joan", 25);
    }

    // Private methods are not overridden, so you can call them directly.
    private void sleep() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
