package appersonal.development.com.appersonaltrainer.model;

/**
 * Created by Danilo on 03/03/2017.
 */

public class Treinos {

    private String nome;
    private Long data;
    private Long dataInicio;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getData() {
        return data;
    }

    public void setData(Long data) {
        this.data = data;
    }

    public Long getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Long dataInicio) {
        this.dataInicio = dataInicio;
    }
}
