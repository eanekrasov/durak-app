@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")
@file:SuppressLint("MutableCollectionMutableState")

package durak.app.ui

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import kotlin.reflect.KProperty

// https://github.com/alorma/Compose-Settings/tree/main/compose-settings-storage-preferences/src/main/kotlin/com/alorma/compose/settings/storage/preferences

interface DuValueState<T> {
    fun reset()
    var value: T
}

inline operator fun <T> DuValueState<T>.getValue(thisObj: Any?, property: KProperty<*>): T = value
inline operator fun <T> DuValueState<T>.setValue(thisObj: Any?, property: KProperty<*>, value: T) {
    this.value = value
}

// region Float Range

@Composable
fun rememberRangeDuState(defaultValue: ClosedFloatingPointRange<Float> = 0f..1f): DuValueState<ClosedFloatingPointRange<Float>> = remember { InMemoryRangeDuValueState(defaultValue) }

@Composable
fun rememberPreferenceRangeSettingState(
    key: String,
    defaultValue: ClosedFloatingPointRange<Float> = 0f..1f,
    preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(LocalContext.current),
): DuValueState<ClosedFloatingPointRange<Float>> = remember { RangePreferenceDuValueState(preferences, key, defaultValue) }

internal class InMemoryRangeDuValueState(private val defaultValue: ClosedFloatingPointRange<Float>) : DuValueState<ClosedFloatingPointRange<Float>> {
    override var value: ClosedFloatingPointRange<Float> by mutableStateOf(defaultValue)
    override fun reset() {
        value = defaultValue
    }
}

internal class RangePreferenceDuValueState(
    private val preferences: SharedPreferences,
    val key: String,
    val defaultValue: ClosedFloatingPointRange<Float> = 0f..1f,
) : DuValueState<ClosedFloatingPointRange<Float>> {
    private fun SharedPreferences.getRange(key: String, defaultValue: ClosedFloatingPointRange<Float>) = getFloat("${key}start", defaultValue.start)..getFloat("${key}end", defaultValue.endInclusive)
    private fun SharedPreferences.Editor.putRange(key: String, value: ClosedFloatingPointRange<Float>) = putFloat("${key}start", value.start).putFloat("${key}end", value.endInclusive)
    private var _value by mutableStateOf(preferences.getRange(key, defaultValue))
    override var value: ClosedFloatingPointRange<Float>
        get() = _value
        set(value) {
            _value = value
            preferences.edit { putRange(key, value) }
        }

    override fun reset() {
        value = defaultValue
    }
}

// endregion

// region Float

@Composable
fun rememberFloatDuState(defaultValue: Float = 0f): DuValueState<Float> = remember { InMemoryFloatDuValueState(defaultValue) }

@Composable
fun rememberPreferenceFloatSettingState(
    key: String,
    defaultValue: Float = 0f,
    preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(LocalContext.current),
): DuValueState<Float> = remember { FloatPreferenceDuValueState(preferences, key, defaultValue) }

internal class InMemoryFloatDuValueState(private val defaultValue: Float) : DuValueState<Float> {
    override var value: Float by mutableStateOf(defaultValue)
    override fun reset() {
        value = defaultValue
    }
}

internal class FloatPreferenceDuValueState(
    private val preferences: SharedPreferences,
    val key: String,
    val defaultValue: Float = 0f,
) : DuValueState<Float> {
    private var _value by mutableStateOf(preferences.getFloat(key, defaultValue))
    override var value: Float
        get() = _value
        set(value) {
            _value = value
            preferences.edit { putFloat(key, value) }
        }

    override fun reset() {
        value = defaultValue
    }
}

// endregion

// region Boolean

@Composable
fun rememberBooleanDuState(defaultValue: Boolean = false): DuValueState<Boolean> = remember { InMemoryBooleanDuValueState(defaultValue) }

@Composable
fun rememberPreferenceBooleanDuState(
    key: String,
    defaultValue: Boolean,
    preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(LocalContext.current),
): DuValueState<Boolean> = remember { BooleanPreferenceDuValueState(preferences, key, defaultValue) }

internal class InMemoryBooleanDuValueState(private val defaultValue: Boolean) : DuValueState<Boolean> {
    override var value: Boolean by mutableStateOf(defaultValue)
    override fun reset() {
        value = defaultValue
    }
}

internal class BooleanPreferenceDuValueState(
    private val preferences: SharedPreferences,
    val key: String,
    val defaultValue: Boolean = false,
) : DuValueState<Boolean> {
    private var _value by mutableStateOf(preferences.getBoolean(key, defaultValue))
    override var value: Boolean
        get() = _value
        set(value) {
            _value = value
            preferences.edit { putBoolean(key, value) }
        }

    override fun reset() {
        value = defaultValue
    }
}

// endregion

// region String

@Composable
fun rememberStringDuState(defaultValue: String = ""): DuValueState<String> = remember { InMemoryStringDuValueState(defaultValue) }

@Composable
fun rememberPreferenceStringSettingState(
    key: String,
    defaultValue: String = "",
    preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(LocalContext.current),
): DuValueState<String> = remember { StringPreferenceDuValueState(preferences, key, defaultValue) }

internal class InMemoryStringDuValueState(private val defaultValue: String) : DuValueState<String> {
    override var value by mutableStateOf(defaultValue)
    override fun reset() {
        value = defaultValue
    }
}

internal class StringPreferenceDuValueState(
    private val preferences: SharedPreferences,
    val key: String?,
    val defaultValue: String = "",
) : DuValueState<String> {
    private var _value by mutableStateOf(preferences.getString(key, defaultValue).orEmpty())
    override var value: String
        get() = _value
        set(value) {
            _value = value
            preferences.edit { putString(key, value) }
        }

    override fun reset() {
        value = defaultValue
    }
}

// endregion

// region Set<Int>

@Composable
fun rememberIntSetDuState(defaultValue: Set<Int> = emptySet()): DuValueState<Set<Int>> = remember { InMemoryIntSetDuValueState(defaultValue) }

@Composable
fun rememberPreferenceIntSetSettingState(
    key: String,
    defaultValue: Set<Int> = emptySet(),
    delimiter: String = "|",
    preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(LocalContext.current),
): DuValueState<Set<Int>> = remember { IntSetPreferenceDuValueState(preferences, key, defaultValue, delimiter) }

internal class InMemoryIntSetDuValueState(private val defaultValue: Set<Int>) : DuValueState<Set<Int>> {
    override var value: Set<Int> by mutableStateOf(defaultValue)
    override fun reset() {
        value = defaultValue
    }
}

internal class IntSetPreferenceDuValueState(
    private val preferences: SharedPreferences,
    val key: String,
    val defaultValue: Set<Int> = emptySet(),
    val delimiter: String = "|",
) : DuValueState<Set<Int>> {
    private fun Set<Int>.toPrefString() = joinToString(delimiter) { "$it" }
    private fun String?.toIntSet() = orEmpty().split(delimiter).filter { it.isNotEmpty() }.map { it.toInt() }.toMutableSet()
    private var _value by mutableStateOf(preferences.getString(key, defaultValue.toPrefString()).toIntSet())
    override var value: Set<Int>
        get() = _value
        set(value) {
            _value = value.toSortedSet()
            preferences.edit { putString(key, value.toPrefString()) }
        }

    override fun reset() {
        value = defaultValue
    }
}

// endregion

// region Int

@Composable
fun rememberIntDuState(defaultValue: Int = -1): DuValueState<Int> = remember { InMemoryIntDuValueState(defaultValue) }

@Composable
fun rememberPreferenceIntSettingState(
    key: String,
    defaultValue: Int = -1,
    preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(LocalContext.current),
): DuValueState<Int> = remember { IntPreferenceDuValueState(preferences, key, defaultValue) }

internal class InMemoryIntDuValueState(private val defaultValue: Int) : DuValueState<Int> {
    override var value: Int by mutableStateOf(defaultValue)
    override fun reset() {
        value = defaultValue
    }
}

internal class IntPreferenceDuValueState(
    private val preferences: SharedPreferences,
    val key: String,
    val defaultValue: Int = 0,
) : DuValueState<Int> {
    private var _value by mutableStateOf(preferences.getInt(key, defaultValue))
    override var value: Int
        get() = _value
        set(value) {
            _value = value
            preferences.edit { putInt(key, value) }
        }

    override fun reset() {
        value = defaultValue
    }
}

// endregion

