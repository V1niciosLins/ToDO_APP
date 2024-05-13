package com.example.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todo.databinding.ActivityMainBinding;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
ActivityMainBinding binding;
private ArrayList<Tarefa> tarefasList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        try {
            tarefasList = catchData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        myRAdapter adap = new myRAdapter(tarefasList, this);

        binding.Recycler.setAdapter(adap);
        binding.Recycler.setLayoutManager(new LinearLayoutManager(this));

        binding.materialButton.setOnClickListener( click->{
            if (binding.EditText.getText().toString().isEmpty()) return;
            tarefasList.add(new Tarefa(Objects.requireNonNull(binding.EditText.getText()).toString(),false));
            adap.newList(tarefasList);
            binding.EditText.getText().clear();
        });
    }
    ArrayList<Tarefa> catchData() throws IOException {
        final File f = new File(getApplicationContext().getFilesDir() +"/Saves");
        final File f2 = new File(getApplicationContext().getFilesDir() +"/Saves/data.json");
        String json = "";
        if (!f.exists())
        {
            f.mkdir();

            if (!f2.exists())
            {
                f2.createNewFile();
            }
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f2));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            json = stringBuilder.toString();
            // Agora 'json' contém o conteúdo do arquivo JSON
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!json.isEmpty()) {
            Tarefa[] t = new Gson().fromJson(json, Tarefa[].class);
            return new ArrayList<>(Arrays.asList(t));
        } else {
            return new ArrayList<>();
        }
    }

}



class Tarefa
{
    @NonNull private String name;
    private boolean isDone;

    public Tarefa() {
        super();
        name = "";
    }

    public Tarefa(String name, boolean isDone) {
        this.name = name;
        this.isDone = isDone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}

class myRAdapter extends RecyclerView.Adapter<myRAdapter.mh>
{
private ArrayList<Tarefa> tarefas;
private Context context;

    public myRAdapter() {
        super();
    }

    public myRAdapter(ArrayList<Tarefa> tarefas, Context context) {
        this.tarefas = tarefas;
        this.context = context;
    }

    @NonNull
    @Override
    public myRAdapter.mh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item,null, false);
        return new mh(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myRAdapter.mh holder, int position) {
        holder.tarefa.setText(tarefas.get(position).getName());
        holder.concluido.setText( tarefas.get(position).isDone()? "Concluído": "Não Concluído");
        holder.remove.setOnClickListener( click->{
            tarefas.remove(tarefas.get(position));
            newList(tarefas);
        });

        holder.tarefa.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.complete, popupMenu.getMenu());

                // Defina um ouvinte de clique de item para o menu de contexto
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Lógica para lidar com os cliques nos itens do menu de contexto
                        switch ((char) item.getTitle().charAt(0)) {
                            case 'C':

                                tarefas.get(holder.getAdapterPosition()).setDone(true);
                                newList(tarefas);

                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popupMenu.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return tarefas.size();
    }

    public void newList(ArrayList<Tarefa> list){
        tarefas = list;
        String json = new Gson().toJson(tarefas);

        try (FileWriter writer = new FileWriter(context.getFilesDir() +"/Saves/data.json")) {
            writer.write(json);
            System.out.println("JSON gravado com sucesso no arquivo.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }
    public class mh extends RecyclerView.ViewHolder
    {
        public TextView tarefa, concluido;
        public ImageView remove;
        public mh(@NonNull View itemView) {
            super(itemView);
            tarefa = itemView.findViewById(R.id.Tarefa);
            concluido = itemView.findViewById(R.id.Status);
            remove = itemView.findViewById(R.id.image);
        }
    }
}