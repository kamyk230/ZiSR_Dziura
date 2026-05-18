import numpy as np
import skfuzzy as fuzz
from skfuzzy import control as ctrl
import tkinter as tk
from tkinter import ttk



# Backend
class SterownikRozmyty:
    def __init__(self):
        self.kolejka = ctrl.Antecedent(np.arange(0, 31, 1), 'kolejka')
        self.ruch = ctrl.Antecedent(np.arange(0, 51, 1), 'ruch')
        self.czas_obecny = ctrl.Antecedent(np.arange(0, 91, 1), 'czas_obecny')
        self.przedluzenie = ctrl.Consequent(np.arange(0, 21, 1), 'przedluzenie')

        self.kolejka['Krótka'] = fuzz.trapmf(self.kolejka.universe, [0, 0, 3, 8])
        self.kolejka['Średnia'] = fuzz.trapmf(self.kolejka.universe, [5, 8, 12, 15])
        self.kolejka['Długa'] = fuzz.trapmf(self.kolejka.universe, [12, 18, 30, 30])

        self.ruch['Mały'] = fuzz.trapmf(self.ruch.universe, [0, 0, 10, 20])
        self.ruch['Średni'] = fuzz.trapmf(self.ruch.universe, [15, 25, 35, 45])
        self.ruch['Duży'] = fuzz.trapmf(self.ruch.universe, [35, 45, 50, 50])

        self.czas_obecny['Krótko'] = fuzz.trapmf(self.czas_obecny.universe, [0, 0, 15, 30])
        self.czas_obecny['Normalnie'] = fuzz.trapmf(self.czas_obecny.universe, [20, 35, 55, 70])
        self.czas_obecny['Zbyt_Długo'] = fuzz.trapmf(self.czas_obecny.universe, [60, 80, 90, 90])

        self.przedluzenie['Brak'] = fuzz.trapmf(self.przedluzenie.universe, [0, 0, 1, 3])
        self.przedluzenie['Krótkie'] = fuzz.trapmf(self.przedluzenie.universe, [2, 5, 9, 12])
        self.przedluzenie['Długie'] = fuzz.trapmf(self.przedluzenie.universe, [10, 16, 20, 20])


        rule1 = ctrl.Rule(self.czas_obecny['Zbyt_Długo'], self.przedluzenie['Brak'])
        rule2 = ctrl.Rule(self.kolejka['Długa'], self.przedluzenie['Brak'])
        rule3 = ctrl.Rule(self.ruch['Mały'], self.przedluzenie['Brak'])


        rule4 = ctrl.Rule(self.ruch['Duży'] & self.kolejka['Krótka'], self.przedluzenie['Długie'])
        rule5 = ctrl.Rule(self.ruch['Duży'] & self.kolejka['Średnia'], self.przedluzenie['Długie'])


        rule6 = ctrl.Rule(self.ruch['Średni'] & self.kolejka['Krótka'], self.przedluzenie['Krótkie'])
        rule7 = ctrl.Rule(self.ruch['Średni'] & self.kolejka['Średnia'], self.przedluzenie['Krótkie'])

        self.system_control = ctrl.ControlSystem([rule1, rule2, rule3, rule4, rule5, rule6, rule7])
        self.symulacja = ctrl.ControlSystemSimulation(self.system_control)

    def oblicz_decyzje(self, in_kolejka, in_ruch, in_czas):
        self.symulacja.input['kolejka'] = in_kolejka
        self.symulacja.input['ruch'] = in_ruch
        self.symulacja.input['czas_obecny'] = in_czas
        try:
            self.symulacja.compute()
            wynik = self.symulacja.output['przedluzenie']
            max_pocz = 0
            etykieta = "Nieznana"
            for term in self.przedluzenie.terms:
                pocz = fuzz.interp_membership(self.przedluzenie.universe, self.przedluzenie[term].mf, wynik)
                if pocz > max_pocz:
                    max_pocz = pocz
                    etykieta = term
            return wynik, etykieta
        except Exception as e:
            return 0.0, "BŁĄD: BRAK REGUŁY"



# GUI

class TrafficApp:
    def __init__(self, root):
        self.root = root
        self.root.title("Sterownik skrzyżowania")
        self.root.geometry("1000x650")

        self.backend = SterownikRozmyty()

        self.cars_horizontal_left = []
        self.cars_horizontal_right = []
        self.cars_vertical_top = []
        self.cars_vertical_bot = []

        self.FPS = 30
        self.frame_time_ms = int(1000 / self.FPS)

        main_frame = ttk.Frame(root, padding="10")
        main_frame.pack(fill=tk.BOTH, expand=True)

        left_col = ttk.Frame(main_frame)
        left_col.pack(side=tk.LEFT, fill=tk.Y, padx=(0, 20))

        right_col = ttk.Frame(main_frame)
        right_col.pack(side=tk.RIGHT, fill=tk.BOTH, expand=True)

        style = ttk.Style()
        style.configure("Header.TLabel", font=("Helvetica", 14, "bold"), foreground="#2c3e50")
        style.configure("Result.TLabel", font=("Helvetica", 16, "bold"), foreground="#c0392b")

        ttk.Label(left_col, text="Panel sterowania", style="Header.TLabel").pack(pady=(0, 20))

        inputs_frame = ttk.LabelFrame(left_col, text=" Droga", padding="15")
        inputs_frame.pack(fill=tk.X, pady=10)

        ttk.Label(inputs_frame, text="Docelowa kolejka (czerwone):").pack(anchor=tk.W)
        self.sl_kolejka = ttk.Scale(inputs_frame, from_=0, to=15, orient=tk.HORIZONTAL, command=self.update_values)
        self.sl_kolejka.pack(fill=tk.X, pady=(0, 5))
        self.lbl_val_kolejka = ttk.Label(inputs_frame, text="5 aut", font=("Helvetica", 10, "italic"))
        self.lbl_val_kolejka.pack(anchor=tk.E)
        self.sl_kolejka.set(5)

        ttk.Label(inputs_frame, text="Natężenie ruchu (zielone):").pack(anchor=tk.W, pady=(10, 0))
        self.sl_ruch = ttk.Scale(inputs_frame, from_=0, to=50, orient=tk.HORIZONTAL, command=self.update_values)
        self.sl_ruch.pack(fill=tk.X, pady=(0, 5))
        self.lbl_val_ruch = ttk.Label(inputs_frame, text="20 aut/min", font=("Helvetica", 10, "italic"))
        self.lbl_val_ruch.pack(anchor=tk.E)
        self.sl_ruch.set(20)

        ttk.Label(inputs_frame, text="Czas obecnego zielonego:").pack(anchor=tk.W, pady=(10, 0))
        self.sl_czas = ttk.Scale(inputs_frame, from_=0, to=90, orient=tk.HORIZONTAL, command=self.update_values)
        self.sl_czas.pack(fill=tk.X, pady=(0, 5))
        self.lbl_val_czas = ttk.Label(inputs_frame, text="30 sekund", font=("Helvetica", 10, "italic"))
        self.lbl_val_czas.pack(anchor=tk.E)
        self.sl_czas.set(30)

        results_frame = ttk.LabelFrame(left_col, text=" Logika ", padding="15")
        results_frame.pack(fill=tk.X, pady=20)

        ttk.Label(results_frame, text="System oblicza na bieżąco...", font=("Helvetica", 10, "italic")).pack(pady=10)

        ttk.Label(results_frame, text="Przedłużenie (COG):").pack()
        self.lbl_res_num = ttk.Label(results_frame, text="-- s", style="Result.TLabel")
        self.lbl_res_num.pack(pady=(5, 10))

        ttk.Label(results_frame, text="Interpretacja logiczna:").pack()
        self.lbl_res_word = ttk.Label(results_frame, text="--", font=("Helvetica", 12, "bold"), foreground="#2980b9")
        self.lbl_res_word.pack()

        ttk.Label(right_col, text="Symulacja", style="Header.TLabel").pack(pady=(0, 10))

        self.canvas = tk.Canvas(right_col, bg="#2c3e50", highlightthickness=2, highlightbackground="#34495e")
        self.canvas.pack(fill=tk.BOTH, expand=True)

        self.root.after(50, self.game_loop)

    def update_values(self, event=None):
        if hasattr(self, 'lbl_val_kolejka'):
            self.lbl_val_kolejka.config(text=f"{int(self.sl_kolejka.get())} aut")

        if hasattr(self, 'lbl_val_ruch'):
            val = int(self.sl_ruch.get())
            val = val - (val % 2)
            self.lbl_val_ruch.config(text=f"{val} aut/min")

        if hasattr(self, 'lbl_val_czas'):
            self.lbl_val_czas.config(text=f"{int(self.sl_czas.get())} sekund")

    def run_fuzzy_logic(self):
        v_kolejka = len(self.cars_vertical_top) + len(self.cars_vertical_bot)

        v_ruch = int(self.sl_ruch.get())
        v_ruch = v_ruch - (v_ruch % 2)

        v_czas = int(self.sl_czas.get())

        wynik_num, wynik_word = self.backend.oblicz_decyzje(v_kolejka, v_ruch, v_czas)

        if wynik_word.startswith("BŁĄD"):
            self.lbl_res_num.config(text="BŁĄD", foreground="red")
            self.lbl_res_word.config(text=wynik_word, foreground="red")
        else:
            self.lbl_res_num.config(text=f"{wynik_num:.2f} s", foreground="#c0392b")
            self.lbl_res_word.config(text=f"{wynik_word.upper()}", foreground="#2980b9")

    def game_loop(self):
        w = self.canvas.winfo_width()
        h = self.canvas.winfo_height()

        if w > 50:
            self.update_physics(w, h)
            self.draw_scene(w, h)
            self.run_fuzzy_logic()

        self.root.after(self.frame_time_ms, self.game_loop)

    def update_physics(self, w, h):
        target_kolejka = int(self.sl_kolejka.get())

        val_ruch = int(self.sl_ruch.get())
        target_ruch = val_ruch - (val_ruch % 2)

        road_w = 120

        chance_per_frame = (target_ruch / 60.0) / self.FPS / 2.0
        max_cars_on_screen = 15

        if len(self.cars_horizontal_left) < max_cars_on_screen and np.random.rand() < chance_per_frame:
            self.cars_horizontal_left.append({'x': -40, 'y': h / 2 + 5, 'speed': np.random.uniform(4, 7)})

        for car in self.cars_horizontal_left:
            car['x'] += car['speed']

        if len(self.cars_horizontal_right) < max_cars_on_screen and np.random.rand() < chance_per_frame:
            self.cars_horizontal_right.append(
                {'x': w + 40, 'y': h / 2 - road_w / 2 + 10, 'speed': -np.random.uniform(4, 7)})

        for car in self.cars_horizontal_right:
            car['x'] += car['speed']

        self.cars_horizontal_left = [c for c in self.cars_horizontal_left if c['x'] < w + 50]
        self.cars_horizontal_right = [c for c in self.cars_horizontal_right if c['x'] > -50]

        current_kolejka = len(self.cars_vertical_top) + len(self.cars_vertical_bot)

        if current_kolejka < target_kolejka and np.random.rand() < 0.1:
            if len(self.cars_vertical_top) <= len(self.cars_vertical_bot):
                self.cars_vertical_top.append({'x': w / 2 - road_w / 2 + 10, 'y': -40, 'speed': 4})
            else:
                self.cars_vertical_bot.append({'x': w / 2 + 5, 'y': h + 40, 'speed': -4})

        while len(self.cars_vertical_top) + len(self.cars_vertical_bot) > target_kolejka:
            if len(self.cars_vertical_top) >= len(self.cars_vertical_bot) and len(self.cars_vertical_top) > 0:
                self.cars_vertical_top.pop()
            elif len(self.cars_vertical_bot) > 0:
                self.cars_vertical_bot.pop()

        q_top_len = 0
        for car in self.cars_vertical_top:
            target_y = (h / 2 - road_w / 2 - 25) - (q_top_len * 30)
            if car['y'] < target_y:
                car['y'] += car['speed']
            else:
                car['y'] = target_y
            q_top_len += 1

        q_bot_len = 0
        for car in self.cars_vertical_bot:
            target_y = (h / 2 + road_w / 2 + 5) + (q_bot_len * 30)
            if car['y'] > target_y:
                car['y'] += car['speed']
            else:
                car['y'] = target_y
            q_bot_len += 1

    def draw_scene(self, w, h):
        self.canvas.delete("all")
        road_w = 120

        self.canvas.create_rectangle(w / 2 - road_w / 2, 0, w / 2 + road_w / 2, h, fill="#7f8c8d", outline="")
        self.canvas.create_rectangle(0, h / 2 - road_w / 2, w, h / 2 + road_w / 2, fill="#95a5a6", outline="")

        self.canvas.create_line(w / 2, 0, w / 2, h, fill="white", dash=(15, 15), width=2)
        self.canvas.create_line(0, h / 2, w, h / 2, fill="white", dash=(15, 15), width=2)
        self.canvas.create_rectangle(w / 2 - road_w / 2, h / 2 - road_w / 2, w / 2 + road_w / 2, h / 2 + road_w / 2,
                                     fill="#7f8c8d", outline="")

        self.canvas.create_oval(w / 2 - road_w / 2 - 25, h / 2 - road_w / 2 - 25, w / 2 - road_w / 2 - 5,
                                h / 2 - road_w / 2 - 5, fill="#e74c3c", outline="black")
        self.canvas.create_oval(w / 2 + road_w / 2 + 5, h / 2 + road_w / 2 + 5, w / 2 + road_w / 2 + 25,
                                h / 2 + road_w / 2 + 25, fill="#e74c3c", outline="black")

        self.canvas.create_oval(w / 2 - road_w / 2 - 25, h / 2 + road_w / 2 + 5, w / 2 - road_w / 2 - 5,
                                h / 2 + road_w / 2 + 25, fill="#2ecc71", outline="black")
        self.canvas.create_oval(w / 2 + road_w / 2 + 5, h / 2 - road_w / 2 - 25, w / 2 + road_w / 2 + 25,
                                h / 2 - road_w / 2 - 5, fill="#2ecc71", outline="black")

        for car in self.cars_vertical_top:
            self.canvas.create_rectangle(car['x'], car['y'], car['x'] + 15, car['y'] + 20, fill="#c0392b",
                                         outline="black")

        for car in self.cars_vertical_bot:
            self.canvas.create_rectangle(car['x'], car['y'], car['x'] + 15, car['y'] + 20, fill="#c0392b",
                                         outline="black")

        for car in self.cars_horizontal_left:
            self.canvas.create_rectangle(car['x'], car['y'], car['x'] + 25, car['y'] + 15, fill="#2980b9",
                                         outline="black")

        for car in self.cars_horizontal_right:
            self.canvas.create_rectangle(car['x'], car['y'], car['x'] + 25, car['y'] + 15, fill="#2980b9",
                                         outline="black")


if __name__ == "__main__":
    root = tk.Tk()
    app = TrafficApp(root)
    root.mainloop()