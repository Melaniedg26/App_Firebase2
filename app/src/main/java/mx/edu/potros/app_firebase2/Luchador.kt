package mx.edu.potros.app_firebase2

import android.os.Parcel
import android.os.Parcelable

class Luchador() : Parcelable {
    var id: Int = 0
    var nombre: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        nombre = parcel.readString()
    }

    constructor(id: Int, nombre: String?) : this() {
        this.id = id
        this.nombre = nombre
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nombre)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Luchador> {
        override fun createFromParcel(parcel: Parcel): Luchador {
            return Luchador(parcel)
        }

        override fun newArray(size: Int): Array<Luchador?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return nombre ?: ""
    }
}

