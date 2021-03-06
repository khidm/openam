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
package org.forgerock.openam.authentication.modules.push;

import com.sun.identity.authentication.spi.AuthLoginException;
import com.sun.identity.shared.debug.Debug;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.DatatypeConverter;
import org.forgerock.openam.core.rest.devices.DeviceJsonUtils;
import org.forgerock.openam.core.rest.devices.oath.OathDeviceSettings;
import org.forgerock.openam.core.rest.devices.push.PushDeviceSettings;
import org.forgerock.openam.core.rest.devices.push.PushDevicesDao;
import org.forgerock.util.Reject;

/**
 * Allows for easy access to storing and creating push device profiles.
 *
 * <blockquote>Ah, push it /
 * Push it real good"</blockquote> - Salt-N-Pepa
 *
 * @since 13.5.0
 */
final class Pusher {

    static final String DEVICE_NAME = "Push Device";
    static final int SECRET_HEX_LENGTH = 32;

    private final SecureRandom secureRandom;
    private final PushDevicesDao devicesDao;
    private final Debug debug;
    private final DeviceJsonUtils<PushDeviceSettings> jsonUtils;

    @Inject
    public Pusher(final @Nonnull PushDevicesDao devicesDao,
           final @Nonnull @Named("amAuthPush") Debug debug,
           final @Nonnull SecureRandom secureRandom,
           final @Nonnull DeviceJsonUtils<PushDeviceSettings> jsonUtils) {
        Reject.ifNull(devicesDao, debug, secureRandom);
        this.devicesDao = devicesDao;
        this.debug = debug;
        this.secureRandom = secureRandom;
        this.jsonUtils = jsonUtils;
    }

    /**
     * Creates and saves a fresh device profile for the given user. This will generate a fresh random shared secret
     * for the device, ensuring a valid device profile is present for device registration.
     *
     * @return the generated device profile.
     */
    public PushDeviceSettings createDeviceProfile() {
        byte[] secretBytes = new byte[SECRET_HEX_LENGTH];
        secureRandom.nextBytes(secretBytes);
        String sharedSecret = DatatypeConverter.printHexBinary(secretBytes);

        return new PushDeviceSettings(sharedSecret, DEVICE_NAME);
    }

    /**
     * Saves the Push device settings to the user's profile, overwriting any existing device profile.
     *
     * @param user the username of the user to generate a device profile for. Cannot be null.
     * @param realm the realm of the user. Cannot be null.
     * @param deviceSettings the device profile to save. Cannot be null.
     * @throws AuthLoginException if the device profile cannot be saved.
     */
    public void saveDeviceProfile(@Nonnull String user, @Nonnull String realm,
                                  @Nonnull PushDeviceSettings deviceSettings)
            throws AuthLoginException {
        Reject.ifNull(user, realm, deviceSettings);
        try {
            devicesDao.saveDeviceProfiles(user, realm,
                    jsonUtils.toJsonValues(Collections.singletonList(deviceSettings)));
        } catch (IOException e) {
            debug.error("Pusher.createDeviceProfile(): Unable to save device profile for user {} in realm {}",
                    user, realm, e);
            throw new AuthLoginException(e);
        }
    }

    /**
     * Retrieves all device profiles for this device type from the datastore for the provided user.
     *
     * @param user User whose device profiles to retrieve.
     * @param realm Realm in which the user exists.
     * @return A list of {@link OathDeviceSettings}.
     * @throws IOException If there was issues talking to the datastore.
     */
    public List<PushDeviceSettings> getDeviceProfiles(@Nonnull String user, @Nonnull String realm)
            throws IOException {
        return jsonUtils.toDeviceSettingValues(devicesDao.getDeviceProfiles(user, realm));
    }

}
