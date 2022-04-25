package Lixeira_date;

import java.net.InetAddress;

/**
 *
 * @author jonatas lima
 */
public class Lixeira_Date implements Comparable<Lixeira_Date> {

    private boolean connected;
    private InetAddress address;
    private int porta;
    private boolean coletada;
    private int latitude;
    private int id;
    private int tipo;
    private double capacidade_disponivel;
    private double capacidade_atual;
    private double capacidade_max;
    private boolean bloqueio;
    private int longitude;

    public Lixeira_Date(boolean connected, InetAddress address, int porta, boolean coletada, int latitude, int id, int tipo, double capacidade_disponivel, double capacidade_atual, double capacidade_max, boolean bloqueio, int longitude) {
        this.connected = connected;
        this.address = address;
        this.porta = porta;
        this.coletada = coletada;
        this.latitude = latitude;
        this.id = id;
        this.tipo = tipo;
        this.capacidade_disponivel = capacidade_disponivel;
        this.capacidade_atual = capacidade_atual;
        this.capacidade_max = capacidade_max;
        this.bloqueio = bloqueio;
        this.longitude = longitude;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public int getPorta() {
        return porta;
    }

    public void setPorta(int porta) {
        this.porta = porta;
    }

    public boolean isColetada() {
        return coletada;
    }

    public void setColetada(boolean coletada) {
        this.coletada = coletada;
    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public double getCapacidade_disponivel() {
        return capacidade_disponivel;
    }

    public void setCapacidade_disponivel(double capacidade_disponivel) {
        this.capacidade_disponivel = capacidade_disponivel;
    }

    public double getCapacidade_atual() {
        return capacidade_atual;
    }

    public void setCapacidade_atual(double capacidade_atual) {
        this.capacidade_atual = capacidade_atual;
    }

    public double getCapacidade_max() {
        return capacidade_max;
    }

    public void setCapacidade_max(double capacidade_max) {
        this.capacidade_max = capacidade_max;
    }

    public boolean isBloqueio() {
        return bloqueio;
    }

    public void setBloqueio(boolean bloqueio) {
        this.bloqueio = bloqueio;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Lixeira_Date{" + "connected=" + connected + ", address=" + address + ", porta=" + porta + ", coletada=" + coletada + ", latitude=" + latitude + ", id=" + id + ", tipo=" + tipo + ", capacidade_disponivel=" + capacidade_disponivel + ", capacidade_atual=" + capacidade_atual + ", capacidade_max=" + capacidade_max + ", bloqueio=" + bloqueio + ", longitude=" + longitude + '}';
    }

    /**
     * COMPARA AFIM DE ORDENAR DO MAIOR AO MENOR USANDO O COLLECTIONS.SORT()
     * @param o
     * @return 
     */
    @Override
    public int compareTo(Lixeira_Date o) {
        double porcentThis = ((this.capacidade_atual / this.capacidade_max));
        double porcentOuther = ((o.getCapacidade_atual() / o.getCapacidade_max()));

        switch (Double.valueOf(porcentThis).compareTo(Double.valueOf(porcentOuther))) {
            case -1:
                return 1;
            case 1:
                return -1;
            default:
                return 0;
        }

    }
}
