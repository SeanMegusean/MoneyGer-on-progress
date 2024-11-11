package com.is101.moneyger.Activities;

import android.database.Cursor;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.is101.moneyger.Activities.DatabaseFunctions.WalletDB;
import com.is101.moneyger.Activities.adapter.RecyclerAdapter;
import com.is101.moneyger.Activities.model.RecyclerModel;
import com.is101.moneyger.R;

import java.util.ArrayList;
import java.util.List;

public class Wallet extends Fragment{

    private RecyclerView recyclerView;
    private List<RecyclerModel> recyclerModels = new ArrayList<>();
    private RecyclerAdapter recyclerAdapter;
    private ConstraintLayout btnAddTransaction;

    WalletDB wdb;
    ArrayList<String> id,title, date, amount;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public Wallet() {
    }

    public static Wallet newInstance(String param1, String param2) {
        Wallet fragment = new Wallet();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        recyclerView = view.findViewById(R.id.rv_name);
        btnAddTransaction = view.findViewById(R.id.btnAddTransaction);


        wdb = new WalletDB(getActivity());
        recyclerModels = wdb.getAllTransactions();

        recyclerAdapter = new RecyclerAdapter(getContext(), recyclerModels);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        btnAddTransaction.setOnClickListener(v -> {
            NewTransaction newTransactionFragment = new NewTransaction();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.walletID, newTransactionFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}