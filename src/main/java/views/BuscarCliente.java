package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import models.Cliente;

public class BuscarCliente extends JFrame {

    private JTextField campoBusca;
    private List<Cliente> listaClientes;
    private DefaultTableModel modeloTabela;

    public BuscarCliente(List<Cliente> listaClientes, DefaultTableModel modeloTabela) {
        this.listaClientes = listaClientes;
        this.modeloTabela = modeloTabela;
        criarFormulario();
    }

    private void criarFormulario() {
        setTitle("Buscar Cliente");
        setSize(400, 150);
        setLayout(new BorderLayout());

        JPanel painelBusca = new JPanel(new GridLayout(2, 1));

        painelBusca.add(new JLabel("Digite o nome ou parte do nome do cliente:"));
        campoBusca = new JTextField();
        painelBusca.add(campoBusca);

        add(painelBusca, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();
        JButton btnBuscar = new JButton("Buscar");
        JButton btnCancelar = new JButton("Cancelar");

        painelBotoes.add(btnBuscar);
        painelBotoes.add(btnCancelar);
        add(painelBotoes, BorderLayout.SOUTH);

        btnBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {  buscarCliente();  }
        });

        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {  dispose(); }
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void buscarCliente() {
        String nomeBusca = campoBusca.getText().trim();  // Obtém o nome ou sobrenome buscado

        if (!nomeBusca.isEmpty()) {
            modeloTabela.setRowCount(0);  // Limpa a tabela antes de mostrar os resultados
            int contador = 1;

            // Percorre a lista de clientes e busca pelo nome ou sobrenome
            for (Cliente cliente : listaClientes) {
                if (cliente.getNome().toLowerCase().contains(nomeBusca.toLowerCase()) ||
                        cliente.getSobrenome().toLowerCase().contains(nomeBusca.toLowerCase())) {

                    // Adiciona os clientes encontrados à tabela
                    modeloTabela.addRow(new Object[]{contador++, cliente.getNome(), cliente.getSobrenome(), cliente.getEndereco(), cliente.getTelefone(), cliente.getCreditScore()});
                }
            }

            if (contador == 1) {
                JOptionPane.showMessageDialog(this, "Nenhum cliente encontrado!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, insira um nome ou sobrenome para busca.");
        }
    }
}