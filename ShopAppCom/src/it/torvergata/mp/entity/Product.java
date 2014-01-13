package it.torvergata.mp.entity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable{
	private String id;
	private int quantita;
	private String nome;
	private double prezzoUnitario;
	private String scadenza;
	private int disponibilita;
	private String descrizione;
	private String fileImmagine;
	private boolean isChecked;
	
	public String getFileImmagine() {
		return fileImmagine;
	}
	private Drawable immagine;
	
	

	public Product(String i){
		id=i;
		setQuantita(1);
		nome="";
		prezzoUnitario=0.0;
		
		scadenza="";
		disponibilita=0;
		descrizione="";
		fileImmagine="";
		immagine= null;
		setChecked(false);
	}
	
	
	public void increment(){
		quantita++;
	}
	public void decrement(){
		quantita--;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(id);
		dest.writeString(nome);
		dest.writeDouble(prezzoUnitario);
		dest.writeInt(quantita);
		dest.writeString(scadenza);
		dest.writeInt(disponibilita);
		dest.writeString(descrizione);
		dest.writeString(fileImmagine);		 
		Bitmap bitmap = (Bitmap)((BitmapDrawable) this.immagine).getBitmap();
		dest.writeParcelable(bitmap, flags);
		
	}
	 private Product(Parcel in) {
	     id = in.readString();
	     nome=in.readString();
	     prezzoUnitario=in.readDouble();
	     quantita=in.readInt();
	     scadenza=in.readString();
	     disponibilita=in.readInt();
	     descrizione=in.readString();
	     fileImmagine=in.readString();
	     Bitmap bitmap = (Bitmap)in.readParcelable(getClass().getClassLoader());
	     immagine = new BitmapDrawable(bitmap);
	     
	 }
	 public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
	     public Product createFromParcel(Parcel in) {
	         return new Product(in);
	     }

	     public Product[] newArray(int size) {
	         return new Product[size];
	     }
	 };

	
	
	//Metodi Get
	public Drawable getImmagine() {
		return immagine;
	}

	public String getNome() {
		return nome;
	}
	
	public double getPrezzoUnitario() {
		return prezzoUnitario;
	}
	
	public String getScadenza() {
		return scadenza;
	}

	public int getDisponibilita() {
		return disponibilita;
	}
	
	public String getDescrizione() {
		return descrizione;
	}

	public String getId() {
		return id;
	}
	public double getPrezzoTotale() {
		return prezzoUnitario*quantita;
	}
	 public int getQuantita() {
		return quantita;
	}

	
	//Metodi Set
	public void setImmagine(Drawable immagine) {
		this.immagine = immagine;
	}
	
	public void setFileImmagine(String file_immagine) {
		this.fileImmagine = file_immagine;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	public void setPrezzoUnitario(double prezzo) {
		this.prezzoUnitario = prezzo;
	}
	
	public void setScadenza(String scadenza) {
		this.scadenza = scadenza;
	}

	public void setDisponibilita(int disponibilita) {
		this.disponibilita = disponibilita;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setQuantita(int quantita) {
		this.quantita = quantita;
	}


	public boolean isChecked() {
		return isChecked;
	}


	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	
	
}
