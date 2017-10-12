package com.livelife.pioalert;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CartActivity extends AppCompatActivity {

    static final String tag = CartActivity.class.getSimpleName();

    ImageButton backButton;
    Cart cart;
    int idCom;

    Button bookingButton,buyButton,buyAndCollectButton,changeAddressButton;
    EditText userMessage;

    RecyclerView cartRecyclerView;
    CartRecyclerView cartAdapter;

    TextView subTotal,shipAmount,total,shippingAddress;

    RelativeLayout loadingOverlay;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        idCom = getIntent().getIntExtra("idCom",0);

        if (idCom==0) finish();

        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        cartRecyclerView = (RecyclerView) findViewById(R.id.cartRecyclerView);
        cartRecyclerView.setHasFixedSize(true);
        cartRecyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cartRecyclerView.setLayoutManager(layoutManager);

        userMessage = (EditText) findViewById(R.id.userMessage);

        bookingButton = (Button) findViewById(R.id.bookingButton);
        bookingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadingOverlay.setVisibility(View.VISIBLE);

                boolean sent = WebApi.getInstance().emailPrenotation(cart.companyId,userMessage.getText().toString());

                AlertDialog.Builder dialog = new AlertDialog.Builder(CartActivity.this);

                if (sent) {

                    dialog.setMessage("Verrai contattato via e-mail da "+cart.companyName+" per i dettagli.");
                    dialog.setTitle("La prenotazione è andata a buon fine!");
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            finish();
                        }
                    });

                } else {

                    dialog.setMessage("Riprova più tardi");
                    dialog.setTitle("Si è verificato un errore...");
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            loadingOverlay.setVisibility(View.GONE);
                        }
                    });

                }

                dialog.show();
            }
        });

        buyButton = (Button) findViewById(R.id.buyButton);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkoutWithPaypal();
            }
        });

        buyAndCollectButton = (Button) findViewById(R.id.buyAndCollectButton);
        buyAndCollectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkoutAndCollectLater();
            }
        });

        buyButton.setEnabled(false);
        buyAndCollectButton.setEnabled(false);
        buyButton.setAlpha(0.4f);
        buyAndCollectButton.setAlpha(0.4f);


        changeAddressButton = (Button) findViewById(R.id.changeAddressButton);
        changeAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CartActivity.this,ShippingAddressActivity.class);
                startActivity(i);
            }
        });

        shippingAddress = (TextView) findViewById(R.id.shippingAddress);



        subTotal = (TextView) findViewById(R.id.subTotal);
        shipAmount = (TextView) findViewById(R.id.shipAmount);
        total = (TextView) findViewById(R.id.total);


        loadingOverlay = (RelativeLayout) findViewById(R.id.loadingOverlay);


    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e(tag,"onResume...");

        updateCart();

        if (PioUser.getInstance().shipAddress != null) {
            shippingAddress.setText(PioUser.getInstance().shipSummary);
        }

        if (cart.sellingMethod == 2) {
            buyButton.setVisibility(View.GONE);
            buyAndCollectButton.setVisibility(View.GONE);
        }
        else if(cart.sellingMethod == 1) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    getDhlRate();
                }
            });
        }



    }

    public void updateCart() {
        cart = WebApi.getInstance().basketShow(idCom);

        if (cart.products == null) {
            //finish();
            return;
        }

        if (cartAdapter == null) {
            cartAdapter = new CartRecyclerView(cart.products,this);
            cartRecyclerView.setAdapter(cartAdapter);
        } else {
            cartAdapter.setItems(cart.products);
        }

        subTotal.setText(Utility.getInstance().getFormattedPrice(cart.subTotal));
        //shipAmount.setText(Utility.getInstance().getFormattedPrice(cart.subTotal));
        total.setText(Utility.getInstance().getFormattedPrice(cart.subTotal));

    }

    public void modifyQuantityForItem(final Product p) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.mod_quant_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setPositiveButton("Fatto", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                WebApi.getInstance().basketMove(p.pid,p.quantity);
                updateCart();
            }
        });
        dialogBuilder.setNegativeButton("Rimuovi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                WebApi.getInstance().basketMove(p.pid,0);
                updateCart();
            }
        });

        TextView prodName = (TextView) dialogView.findViewById(R.id.prodName);
        prodName.setText(p.name);


        Button add = (Button) dialogView.findViewById(R.id.addButton);
        Button sub = (Button) dialogView.findViewById(R.id.subButton);

        final TextView quantity = (TextView) dialogView.findViewById(R.id.quantity);
        quantity.setText(""+p.quantity+"");


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (p.quantity < p.available) {
                    p.quantity++;
                }

                quantity.setText(""+p.quantity+"");

            }
        });

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (p.quantity > 0) {
                    p.quantity--;
                }

                quantity.setText(""+p.quantity+"");

            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

    }

    String currentDhlRateId,currentPaypalClientToken,currentDhlTotal;

    void getDhlRate() {



        if(PioUser.getInstance().shipAddress != null) {
            JSONObject rateRequest = WebApi.getInstance().getDhlRate(CartActivity.this, idCom);
            try {
                Log.v(tag, "RATE REQUEST: "+rateRequest.toString(2));

                if (!rateRequest.has("id_rate")) {
                    showErrorDialog();
                    return;
                }

                currentDhlRateId = rateRequest.getString("id_rate");
                currentDhlTotal = rateRequest.getJSONObject("results").getJSONObject("TotalNet").getString("Amount");
                currentPaypalClientToken = rateRequest.getString("payPalClientToken");
                cart.shippingTotal = Double.parseDouble(currentDhlTotal);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.v(tag,"runOnUiThread...");
                        shipAmount.setText(Utility.getInstance().getFormattedPrice(currentDhlTotal));
                        double t = cart.subTotal + Double.parseDouble(currentDhlTotal);
                        total.setText(Utility.getInstance().getFormattedPrice(t));
                        buyButton.setEnabled(true);
                        buyAndCollectButton.setEnabled(true);
                        buyButton.setAlpha(1);
                        buyAndCollectButton.setAlpha(1);


                        buyButton.setText("PAGA E RICEVI A CASA "+Utility.getInstance().getFormattedPrice(t));
                        buyAndCollectButton.setText("PAGA E RITIRA IN NEGOZIO "+Utility.getInstance().getFormattedPrice(cart.subTotal));

                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            showNoShippingDialog();
        }



    }


    public void showNoShippingDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(CartActivity.this);
        dialog.setTitle("Attenzione");
        dialog.setMessage("Non hai specificato nessun indirizzo di spedizione, vuoi specificarne uno?");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                Intent i = new Intent(CartActivity.this,ShippingAddressActivity.class);
                startActivity(i);
            }
        });
        dialog.setNegativeButton("Ignora", null);
        dialog.show();
    }

    public void showErrorDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(CartActivity.this);
        dialog.setTitle("Attenzione");
        dialog.setMessage("Si è verificato un errore, si prega di riprovare");
        dialog.setPositiveButton("OK", null);
        dialog.show();
    }

    void checkoutWithPaypal() {

        if (currentDhlTotal == null) {
            showNoShippingDialog();
            return;
        }

        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(currentPaypalClientToken);

        double t = cart.subTotal+cart.shippingTotal;
        dropInRequest.amount(""+t+"");
        dropInRequest.requestThreeDSecureVerification(true);

        startActivityForResult(dropInRequest.getIntent(this), 1234);

    }

    void checkoutAndCollectLater() {
        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(currentPaypalClientToken);

        double t = cart.subTotal;
        dropInRequest.amount(""+t+"");
        dropInRequest.requestThreeDSecureVerification(true);

        startActivityForResult(dropInRequest.getIntent(this), 3456);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (9999) : {
                if (resultCode == Activity.RESULT_OK) {

                    updateCart();
                }
                break;

            }

            case (1234) : {

                if (resultCode == Activity.RESULT_OK) {
                    DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                    // use the result to update your UI and send the payment method nonce to your server

                    Log.v(tag,"PayPal DropInResult nonce: "+result.getPaymentMethodNonce().getNonce());

                    final String nonce = result.getPaymentMethodNonce().getNonce();
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            double t = cart.subTotal+cart.shippingTotal;
                            JSONObject resp = WebApi.getInstance().paypalTrans(nonce,""+t+"",currentDhlRateId,idCom);

                            try {
                                final boolean responseOk = resp.getBoolean("response");
                                Log.v(tag,"paypalTrans: "+resp.toString(2));

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(responseOk) {

                                            AlertDialog.Builder dialog = new AlertDialog.Builder(CartActivity.this);
                                            dialog.setMessage("L'ordine è andato a buon fine, ti abbiamo mandato una mail di conferma. Controlla la sezione Ordini per controllare la spedizione del tuo acquisto.");
                                            dialog.setTitle("Grazie!");
                                            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                                    finish();
                                                }
                                            });
                                            dialog.show();

                                        } else {

                                            AlertDialog.Builder dialog = new AlertDialog.Builder(CartActivity.this);
                                            dialog.setMessage("Si è verificato un error. Riprova o contatta il gestore della tua carta o servizio.");
                                            dialog.setTitle("Ops!");
                                            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                                    finish();
                                                }
                                            });
                                            dialog.show();

                                        }
                                    }
                                });


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    });

                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // the user canceled

                    Log.v(tag,"PayPal DropInResult: User canceled");

                } else {
                    // handle errors here, an exception may be available in
                    Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);

                    Log.v(tag,"PayPal DropInResult: Error "+error.toString());
                }

                break;
            }
            case (3456): {
                if (resultCode == Activity.RESULT_OK) {
                    DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                    // use the result to update your UI and send the payment method nonce to your server

                    Log.v(tag,"PayPal DropInResult nonce: "+result.getPaymentMethodNonce().getNonce());

                    final String nonce = result.getPaymentMethodNonce().getNonce();
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            double t = cart.subTotal;
                            JSONObject resp = WebApi.getInstance().paypalTransNoShip(nonce,""+t+"",idCom);

                            try {
                                final boolean responseOk = resp.getBoolean("response");
                                Log.v(tag,"paypalTrans: "+resp.toString(2));

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(responseOk) {

                                            AlertDialog.Builder dialog = new AlertDialog.Builder(CartActivity.this);
                                            dialog.setMessage("L'ordine è andato a buon fine, ti abbiamo mandato una mail di conferma. Vai al negozio con un tuo documento d'identità e ritira il tuo acquisto.");
                                            dialog.setTitle("Grazie!");
                                            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                                    finish();
                                                }
                                            });
                                            dialog.show();

                                        } else {

                                            AlertDialog.Builder dialog = new AlertDialog.Builder(CartActivity.this);
                                            dialog.setMessage("Si è verificato un error. Riprova o contatta il gestore della tua carta o servizio.");
                                            dialog.setTitle("Ops!");
                                            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                                    finish();
                                                }
                                            });
                                            dialog.show();

                                        }
                                    }
                                });


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    });

                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // the user canceled

                    Log.v(tag,"PayPal DropInResult: User canceled");

                } else {
                    // handle errors here, an exception may be available in
                    Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);

                    Log.v(tag,"PayPal DropInResult: Error "+error.toString());
                }

                break;
            }
        }
    }
}
