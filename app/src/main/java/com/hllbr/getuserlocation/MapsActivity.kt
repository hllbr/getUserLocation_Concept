package com.hllbr.getuserlocation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.hllbr.getuserlocation.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
  //  var locationManager : LocationManager? = null
    lateinit var locationManager : LocationManager//bu şekilde ifade edersek soru işareti ile bilinmezliğini belirtmemize ve null olarak tanımlamamıza gerek kalmaz
    //LocationManager tüm süreci yönetmek için kullanılan Objenin adı
    lateinit var locationListener : LocationListener//Asıl işi yapan kısım burası bizim için
    //Burdaki tanımlamalardan sonra ifadelerimi onMapReady altında tanımlamam lazım

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
       /* mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))


        mMap = googleMap
        val Amsterdam = LatLng(152.363678,4.8808445)
        mMap.addMarker(MarkerOptions().position(Amsterdam).title("Marker in Amsterdam"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Amsterdam,15f))

        Kullanıcının yerini alarak ne yapabilirim zoomlayarak açabilirim / Kaydedebilirim
        Burada verilen şablonlar benim için yeterli işlevi sağlamıyor benim static olan bu yapıdan kurtulmam ve kullanıcının o anki pozisyonuna erişerek bu konumu kendisine göstermem gerekiyor.
        LocationManager olarak ifade edilen konum yöneticisi birde LocationListener olarak ifade ettiğimiz konum dinleyicisine ihtiyacım oluyor istediğim özellikleri app içerisinde aktif etmek için
        Bu ikisi birlikte çalışıyor.
        LocationManager bütün süreci yönetirken Location Listener konumda bir değişiklik oldumu dinliyor ve bize haber veriyor.
         */
        mMap = googleMap
        //LocationManager'i tanımlamak LocationListeneri tanımlamaya kıyasla daha kolay bir işlemdir.
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager//this operation is casting
/*
Buradaki getSystemService ile birlikte hangi servisleri LocationManager içerisinde kullanmak istediğimi derleyiciye ifade etmem gerekiyor
bu yapı birebir Java içerisinde de bulunuyor.izlenen yol(Yöntem) tamamen aynı
Bu yapı içerisinde kullanabileceğim birçok servis bulunuyor
ulaşılabilirlik servisi / lokasyon servisi /Sensör servisi /Bluetooth Service
Download Service / Display Service / Media Service vb....
Ben kişinin konumunu göstermek istediğim için location Service'ten yararlanmak zorundayım
getSystemService ile aldığım verinin bir tipi yok tipimiz any bizim bunu belirtmemiz gerekiyor
***********************************************

LocationListener is a Abstract Class Example
biz locationListener interface'ini bir obje üzerinden tanımlamamız gerekir.

 */
        locationListener = object :LocationListener{
            //bu yapıyı kurarak LocationListenerin içerinde bulunan fonksiyonlara erişebiliyoruz.

            override fun onLocationChanged(location: Location) {
                //Bu yapı sayesinde konumdaki değişiklikleri takipedebileceğim
                //Bu fonksiyon içersinde bize tanımlı gelen location değişkenini latLng'a çevirebiliyoruz.
                //alt satırlarda alınan izinlerden sonra kullanıcı konumu almak işçin gerekli bilgiler bu yapıya geliyor fakat bu yapı kullanıcı konumu değiştiğinde aktif olması üzerine kurulu  Kullanıcı konumu değiştiğinde yapılmak istenenleri bu alana yazıyorum örneğin konmuu harita eklentisinde bir marker ile ifade etmek

                if(location != null){
                //Konum boşmu diye bir kontrol gerçekleştirdim
                    val userLocation = LatLng(location.latitude,location.longitude)
                    mMap.addMarker(MarkerOptions().position(userLocation).title("Marker in User Location"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15f))
                    //konum değiştikçe yeni bir marker ekleniyor bir önceki markeri silerek haritada tek bir marker eklemek istersek yapının başıdan mMap.clear() dememiz yeterli olacaktir.

                }

            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                super.onStatusChanged(provider, status, extras)
            }

            override fun onProviderEnabled(provider: String) {
                super.onProviderEnabled(provider)
            }

            override fun onProviderDisabled(provider: String) {
                super.onProviderDisabled(provider)
            }
            /*
            Kullanıcı konumunu almak için öncelikli olarak Androidin bize şart koştuğu konum iznini kullanıcıdan almamız gerekiyor
            Almamız gereken iznin derecesi yüksek seviyede bu izin kullanıcıdan alınmadan konum erişimi sağlayamayız.

             */


        }
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                //you need chosen Manifest in android
                //not acces
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
            }else{
                //acces
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,15f,locationListener)
            }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        /*
        Kullanıcıdan bir izin istendikten sonra kullanıcının cevabını geri veriyordu bu yapı bana
        İzin verildiğinde o an birşey olmasını istiyorsam olmasını istediğim işlemi yada işlemleri onRequestPermission içerisinde ifade etmem gerekiyor.
        Bu izin bir kere alınacak her uygulamaya giriş yapıldığında istenmeyecek
        bu alan içerisinde ifade edilen alan ile aynı diyeibilirim.
        Bu alanda request code kontrol edilecek farklı izinler varsa bunları birbirlerinden ayrıştırmamız için gerekli oluyor.

         */
        if(requestCode == 1){
            //request code eğer okeyse grandresult dizisi içerisinde bana verilmiş izinleri kontrol ediyorum verilen bir sonuç var mı ?
            if(grantResults.size > 0){
                //bu kısımda okeyse şimdi asıl kontrolümü yapabilirim.
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    //eğer izin verildiyse
                    //Kullanıcı konumunu almak için Kullanıcı yöneticisiyle / konum yönetici / locationManager ile birlikte alıyoruz biden çok şekilde tarif edilebiliyor.
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,15f,locationListener)//requestLocationUpdates = kullanıcının konumunu güncelle olarak türkçe anlam ve ifadesi bulunuyor diyebilirim.
                    //Bu yapı bana bir kaç soru soruyor birinci olarak bu konumu sağlayan ne GPS ile bu işlemi gerçekleştirebilirim fakat birçok alternatif yöntemim bulunuyor.
                    //Network Provider ile kablosuz ağ ile konum bulmak istersem çok extream bir durum sayılmaz kişi gps alanı dışında zorlu bir durumla karşılaştığında bu tip bir yapı ile bulunduğu konumdan haberdar edilebilir.
                    //ikinci olarak minimumTime ve minimumDistance verileri bizden isteniyor.Bunların istenmesinin sebebi ise kullanıcı konumunun ne kadar zaman ve ne sıklıkla gerçekleştirilmesini istediğimizi anlamak için bize soruluyor .
                    //eğer 0 yazarsam her saniye benim konum bilgimi güncelleme demiş olurum bunun artıları kadar eksileride bulunmakta batarya kullanımı gibi durumlarda dezajavtaj yaratmakta
                    //minimumDistance içinde aynı durum geçerli çok kısa mesafelerde konum bilgisini değiştirebiliriz.
                    //ilginç bir senaryo olarak minimumTime uzun tutulup minimumDistance daha kısa mesafeler olarak ayarlanabilir. örneğin bir dakika süre olarak ifade edilip 100 metre gibi bir mesafe tanımlanabilir değişiklikleri kontrol etmek ve yansıtmak için
                    //Son olarak bu bilgilerin nereye aktarılacağı bize soruluyor biz bunun için bir dinleyici yapsıı kurduk LocationListener bizim için bu verilerin aktarılacağı sınıf locationListener ise verilerin aktarılacağı objemiz olarak tanımlı bulunmakta

                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}