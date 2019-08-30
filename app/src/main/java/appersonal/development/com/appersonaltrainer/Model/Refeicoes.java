package appersonal.development.com.appersonaltrainer.Model;

public class Refeicoes {

    private String refeicao;
    private String hora;
    private String minuto;
    private long time;

    public String getRefeicao() {
        return refeicao;
    }

    public void setRefeicao(String refeicao) {
        this.refeicao = refeicao;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getMinuto() {
        return minuto;
    }

    public void setMinuto(String minuto) {
        this.minuto = minuto;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    @Override
//    public int compareTo(@NonNull Refeicoes o) {
//        return Long.compare(this.time, o.time);
//    }
}
