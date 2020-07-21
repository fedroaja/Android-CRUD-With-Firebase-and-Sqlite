package umn.ac.id.uas2020_mobile_cl_00000021661_fedro_soal01.Model;

public class Mahasiswa {

    private String ID;
    private String NIM;
    private String Nama;
    private String Prodi;
    private String Angkatan;
    private String Biografi;
    private String ImageURL;


    public Mahasiswa(String ID, String NIM, String nama, String prodi, String angkatan, String biografi, String imageURL) {
        this.ID = ID;
        this.NIM = NIM;
        Nama = nama;
        Prodi = prodi;
        Angkatan = angkatan;
        Biografi = biografi;
        ImageURL = imageURL;
    }

    public Mahasiswa() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public String getNIM() {
        return NIM;
    }

    public void setNIM(String NIM) {
        this.NIM = NIM;
    }

    public String getNama() {
        return Nama;
    }

    public void setNama(String nama) {
        Nama = nama;
    }

    public String getProdi() {
        return Prodi;
    }

    public void setProdi(String prodi) {
        Prodi = prodi;
    }

    public String getAngkatan() {
        return Angkatan;
    }

    public void setAngkatan(String angkatan) {
        Angkatan = angkatan;
    }

    public String getBiografi() {
        return Biografi;
    }

    public void setBiografi(String biografi) {
        Biografi = biografi;
    }
}
