
/****************************************************************
 * Specifies the variables available in the FREX shader API
 * to describe accessibility preferences that affect visuals.
 *
 * See FREX Shader API.md for license and general informaiton.
 ***************************************************************/

/*
 * FOV Effects.
 *
 * Defaults to 1.0 when unavailable.
 */
const float frx_fovEffects;

/*
 * Distortion Effects.
 *
 * Defaults to 1.0 when unavailable.
 */
const float frx_distortionEffects;

/*
 * Hide Lightning Flashes. 1 when ON, 0 otherwise.
 *
 * This setting's effect is precomputed in frx_skyFlashStrength.
 * No need to multiply it manually to that variable.
 *
 * Defaults to 0 when unavailable.
 */
const int frx_hideLightningFlashes;

/*
 * Darkness Pulsing.
 *
 * This setting's effect is precomputed in frx_darknessEffectFactor.
 * No need to blend it manually to that variable.
 *
 * Defaults to 1.0 when unavailable.
 */
const float frx_darknessPulsing;

/*
 * High Contrast. 1 when ON, 0 otherwise.
 *
 * Defaults to 0 when unavailable.
 */
const int frx_highContrast;

/*
 * Damage Tilt.
 *
 * Defaults to 1.0 when unavailable.
 */
const float frx_damageTilt;

/*
 * Glint Strength.
 *
 * Defaults to 1.0 when unavailable.
 */
const float frx_glintStrength;

/*
 * Glint Speed.
 *
 * Defaults to 1.0 when unavailable.
 */
const float frx_glintSpeed;
