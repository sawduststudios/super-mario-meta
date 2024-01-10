import os
import glob
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd

# Specify the subfolder
subfolder = "elective-data"

# Get all CSV files in the specified subfolder
files = glob.glob(os.path.join(subfolder, "*.csv"))

if not files:
    print(f"No CSV files found in the '{subfolder}' subfolder.")
    exit()

# Define the values for search steps and time to finish weight
search_steps = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20]
right_border_window = [48, 96, 124, 150, 176, 200, 224, 248, 272, 296, 320]

# Initialize data structures to store win rate and run time values
win_rate_data = np.zeros((len(search_steps), len(right_border_window)))
run_time_data = np.zeros((len(search_steps), len(right_border_window)))

test_files_num = 0
total_runs = 15 + 3 * 100
# Loop through CSV files and extract data
for file_index, file in enumerate(files):
    with open(file, 'r') as f:
        # Read the first two lines to determine SS and TTFW values
        ss_line = f.readline().strip()
        rbw_line = f.readline().strip()

        search_steps_value = int(ss_line.split(":")[-1])
        rbw_value = float(rbw_line.split(":")[-1])

        if search_steps_value == 176 and search_steps_value == 6:
            test_files_num += 1

        # Read the rest of the file as a CSV
        df = pd.read_csv(f)

    # add number of true values in "win/fail" column
    win_rate_data[search_steps.index(search_steps_value), right_border_window.index(rbw_value)] += df[
        "win/fail"].sum()
    run_time_data[search_steps.index(search_steps_value), right_border_window.index(rbw_value)] += df[
        "run time"].sum()

print(test_files_num)
# Calculate average win rate and run time
win_rate_data /= total_runs
run_time_data /= total_runs

# Plot average win rate heatmap with numeric values
plt.figure(figsize=(8, 6))
cax = plt.imshow(win_rate_data, cmap="viridis", aspect="auto", origin="lower", interpolation="nearest")
plt.colorbar(label="Average Win Rate")

# Add text annotations to the heatmap cells
for i in range(len(search_steps)):
    for j in range(len(right_border_window)):
        plt.text(j, i, f'{win_rate_data[i, j]:.2f}', ha='center', va='center', color='w')

plt.xticks(np.arange(len(right_border_window)), right_border_window)
plt.yticks(np.arange(len(search_steps)), search_steps)
plt.xlabel("Right border window width (px)")
plt.ylabel("Search Steps")
plt.title("Average Win Rate Dependence on Parameters")
plt.show()

# Plot average run time heatmap without numeric values
plt.figure(figsize=(8, 6))
cax = plt.imshow(run_time_data, cmap="viridis", aspect="auto", origin="lower", interpolation="nearest")
plt.colorbar(label="Average Run Time (ms)")

plt.xticks(np.arange(len(right_border_window)), right_border_window)
plt.yticks(np.arange(len(search_steps)), search_steps)
plt.xlabel("Right border window width (px)")
plt.ylabel("Search Steps")
plt.title("Average Run Time Dependence on Parameters")
plt.show()
