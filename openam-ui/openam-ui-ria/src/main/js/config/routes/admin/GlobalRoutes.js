/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2016 ForgeRock AS.
 */

define("config/routes/admin/GlobalRoutes", [
    "lodash"
], (_) => {
    const routes = {
        listAuthenticationSettings: {
            view: "org/forgerock/openam/ui/admin/views/configuration/authentication/ListAuthenticationView",
            url: /configure\/authentication/,
            pattern: "configure/authentication",
            role: "ui-global-admin",
            navGroup: "admin"
        },
        editAuthenticationSettings: {
            view: "org/forgerock/openam/ui/admin/views/configuration/authentication/EditConfigurationView",
            url: /configure\/authentication\/([^\/]+)/,
            pattern: "configure/authentication/?",
            role: "ui-global-admin",
            navGroup: "admin"
        },
        listGlobalServices: {
            view: "org/forgerock/openam/ui/admin/views/configuration/global/ListGlobalServicesView",
            url: /configure\/global-services/,
            pattern: "configure/global-services",
            role: "ui-global-admin",
            navGroup: "admin"
        },
        editGlobalService: {
            view: "org/forgerock/openam/ui/admin/views/configuration/global/EditConfigurationView",
            url: /configure\/global-services\/([^\/]+)/,
            pattern: "configure/global-services/?",
            role: "ui-global-admin",
            navGroup: "admin"
        },
        listSites: {
            view: "org/forgerock/openam/ui/admin/views/deployment/sites/ListSitesView",
            url: /deployment\/sites/,
            pattern: "deployment/sites",
            role: "ui-realm-admin",
            navGroup: "admin"
        },
        editSite: {
            view: "org/forgerock/openam/ui/admin/views/deployment/sites/EditSiteView",
            url: /deployment\/sites\/edit\/([^\/]+)/,
            pattern: "deployment/sites/edit/?",
            role: "ui-global-admin",
            navGroup: "admin"
        },
        newSite: {
            view: "org/forgerock/openam/ui/admin/views/deployment/sites/NewSiteView",
            url: /deployment\/sites\/new/,
            pattern: "deployment/sites/new",
            role: "ui-global-admin",
            navGroup: "admin"
        },
        listServers: {
            view: "org/forgerock/openam/ui/admin/views/deployment/servers/ListServersView",
            url: /deployment\/servers/,
            pattern: "deployment/servers$",
            role: "ui-realm-admin",
            navGroup: "admin"
        },
        newServer: {
            view: "org/forgerock/openam/ui/admin/views/deployment/servers/NewServerView",
            url: /deployment\/servers\/new/,
            pattern: "deployment/servers/new",
            role: "ui-realm-admin",
            navGroup: "admin"
        }
    };

    // Add routes for "Server Edit" tree navigation
    _.each(["general", "security", "session", "sdk", "cts", "uma", "advanced", "directoryConfiguration"], (suffix) => {
        routes[`editServer${_.capitalize(suffix)}`] = {
            view: "org/forgerock/openam/ui/admin/views/deployment/servers/EditServerTreeNavigationView",
            page: "org/forgerock/openam/ui/admin/views/deployment/servers/EditServerView",
            url: new RegExp(`deployment/servers/([^\/]+)/(${suffix})`),
            pattern: `deployment/servers/?/${suffix}`,
            role: "ui-global-admin",
            navGroup: "admin",
            forceUpdate: true
        };
    });

    // Add routes for "Server Defaults" tree navigation
    _.each(["general", "security", "session", "sdk", "cts", "uma", "advanced"], (suffix) => {
        routes[`editServerDefaults${_.capitalize(suffix)}`] = {
            view: "org/forgerock/openam/ui/admin/views/configuration/server/EditServerDefaultsTreeNavigationView",
            page: "org/forgerock/openam/ui/admin/views/configuration/server/ServerDefaultsView",
            url: new RegExp(`configure/server-defaults/(${suffix})`),
            pattern: `configure/server-defaults/${suffix}`,
            role: "ui-global-admin",
            navGroup: "admin",
            forceUpdate: true
        };
    });

    return routes;
});
