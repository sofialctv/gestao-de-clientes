package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import models.BufferDeClientes;
import models.Cliente;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PesquisarCliente extends JFrame {

    private JTextField campoBusca;
    private BufferDeClientes bufferDeClientes;
    private DefaultTableModel tableModel;
    private String arquivoSelecionado;
    private final int TAMANHO_BUFFER = 10000; // Tamanho do buffer (ajustável)

    public PesquisarCliente(BufferDeClientes bufferDeClientes, DefaultTableModel tableModel, String arquivoSelecionado) {
        this.bufferDeClientes = bufferDeClientes;
        this.tableModel = tableModel;
        this.arquivoSelecionado = arquivoSelecionado;

        criarFormulario();
    }

    private void criarFormulario() {
        setTitle("Buscar Cliente");
        setSize(400, 150);
        setLayout(new BorderLayout());

        JPanel painelBusca = new JPanel(new GridLayout(2, 1));
        painelBusca.add(new JLabel("Digite o termo de busca (nome ou sobrenome):"));
        campoBusca = new JTextField();
        painelBusca.add(campoBusca);

        add(painelBusca, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();
        JButton btnBuscar = new JButton("Buscar");
        JButton btnCancelar = new JButton("Cancelar");

        painelBotoes.add(btnBuscar);
        painelBotoes.add(btnCancelar);
        add(painelBotoes, BorderLayout.SOUTH);

        // Ação do botão "Buscar"
        btnBuscar.addActionListener(e -> {
            String termoPesquisa = campoBusca.getText().toLowerCase(); // Termo de pesquisa em minúsculas
            if (!termoPesquisa.isEmpty()) {
                // Chama a pesquisa
                pesquisarCliente(termoPesquisa);
            } else {
                JOptionPane.showMessageDialog(null, "Por favor, digite um termo de pesquisa.");
            }
        });

        // Ação do botão "Cancelar"
        btnCancelar.addActionListener(e -> dispose()); // Fecha o formulário de pesquisa

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void pesquisarCliente(String termoPesquisa) {
        if (arquivoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Nenhum arquivo selecionado");
            return;
        }

        bufferDeClientes.inicializaBuffer("leitura", arquivoSelecionado);
        Cliente[] clientes = bufferDeClientes.proximosClientes(TAMANHO_BUFFER);
        List<Cliente> clientesEncontrados = new ArrayList<>();

        while (clientes != null && clientes.length > 0) {
            for (Cliente cliente : clientes) {
                if (cliente != null) {
                    // Verifica se o nome ou sobrenome do cliente contém o termo de pesquisa (case insensitive)
                    if (cliente.getNome().toLowerCase().contains(termoPesquisa) || cliente.getSobrenome().toLowerCase().contains(termoPesquisa)) {
                        clientesEncontrados.add(cliente);
                    }
                }
            }
            // Continuar buscando nos próximos registros, caso existam
            clientes = bufferDeClientes.proximosClientes(TAMANHO_BUFFER);
        }

        if (clientesEncontrados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum cliente encontrado.");
        } else {
            // Limpa a tabela e adiciona os clientes encontrados
            tableModel.setRowCount(0);
            for (Cliente cliente : clientesEncontrados) {
                tableModel.addRow(new Object[]{
                        tableModel.getRowCount() + 1,
                        cliente.getNome(),
                        cliente.getSobrenome(),
                        cliente.getTelefone(),
                        cliente.getEndereco(),
                        cliente.getCreditScore()
                });
            }
        }
    }
}
