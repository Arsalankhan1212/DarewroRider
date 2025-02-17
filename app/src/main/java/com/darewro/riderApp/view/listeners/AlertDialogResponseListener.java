package com.darewro.riderApp.view.listeners;

public interface AlertDialogResponseListener {
    void OnSuccess();
    void OnCancel();

    void OnSuccess(Object object);
    void OnCancel(Object object);

    void OnSuccess(int alertId);
    void OnCancel(int alertId);

    void OnSuccess(int alertId, Object object);
    void OnCancel(int alertId, Object object);

    void OnSuccess(int alertId, Object object, Object object2);
    void OnCancel(int alertId, Object object, Object object2);
}
