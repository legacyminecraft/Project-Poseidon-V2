package net.minecraft.server;

class NetworkReaderThread extends Thread {

    final NetworkManager a;

    NetworkReaderThread(NetworkManager networkmanager, String s) {
        super(s);
        this.a = networkmanager;
    }

    public void run() {
        Object object = NetworkManager.a;

        synchronized (NetworkManager.a) {
            ++NetworkManager.b;
        }

        while (true) {
            boolean flag = false;

            try {
                flag = true;
                if (!NetworkManager.a(this.a)) {
                    flag = false;
                    break;
                }

                if (NetworkManager.b(this.a)) {
                    flag = false;
                    break;
                }

                while (NetworkManager.c(this.a)) {
                    ;
                }

                // Poseidon start - do not sleep at all
                /*try {
                    sleep(100L);
                } catch (InterruptedException interruptedexception) {
                    ;
                }*/
                // Poseidon end
            } finally {
                if (flag) {
                    Object object1 = NetworkManager.a;

                    synchronized (NetworkManager.a) {
                        --NetworkManager.b;
                    }
                }
            }
        }

        object = NetworkManager.a;
        synchronized (NetworkManager.a) {
            --NetworkManager.b;
        }
    }
}
