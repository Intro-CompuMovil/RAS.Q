class User(
    var name: String = "",
    var lastName: String = "",
    var available: Boolean = false,
    var email: String = "",
    var password: String = "",
    var cellphone: String = "",
    var address: String = "",
    var verified: Boolean = false,
    var uid: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
) : java.io.Serializable {
    // Resto del código de la clase
}
