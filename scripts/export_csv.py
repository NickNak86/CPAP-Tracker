#!/usr/bin/env python3
"""
scripts/export_csv.py
A simple export tool for BiPAP usage data.

Usage:
    python scripts/export_csv.py --input example_data.json --output usage_report.csv
"""
import argparse
import json
import logging
import os
from typing import List, Dict

try:
    import pandas as pd  # type: ignore
    _HAS_PANDAS = True
except Exception:
    _HAS_PANDAS = False

def export_usage_to_csv(records: List[Dict], filename: str, columns=None):
    """
    Export BiPAP usage records to CSV format.

    Args:
        records: List of dictionaries containing usage data
        filename: Output CSV filename
        columns: List of column names to export (uses defaults if None)
    """
    default_cols = ["date", "usage_hours", "AHI", "mask_leak_rate", "pressure_settings"]
    cols = columns or default_cols

    if not records:
        raise ValueError("No data to export")

    os.makedirs(os.path.dirname(filename) or ".", exist_ok=True)

    if _HAS_PANDAS:
        # Use pandas if available (faster, better handling)
        df = pd.DataFrame(records)
        for c in cols:
            if c not in df.columns:
                df[c] = None
        df.to_csv(filename, columns=cols, index=False)
    else:
        # Fallback to standard csv module
        import csv
        with open(filename, "w", newline="", encoding="utf-8") as fh:
            writer = csv.DictWriter(fh, fieldnames=cols)
            writer.writeheader()
            for r in records:
                row = {c: r.get(c, "") for c in cols}
                writer.writerow(row)

    logging.getLogger(__name__).info("Exported %d records to %s", len(records), filename)
    print(f"Successfully exported {len(records)} records to {filename}")


def main():
    """CLI entry point for CSV export."""
    parser = argparse.ArgumentParser(description="Export BiPAP usage data to CSV")
    parser.add_argument("--input", required=True, help="Input JSON file with usage records")
    parser.add_argument("--output", default="usage_export.csv", help="Output CSV filename")
    parser.add_argument("--columns", help="Comma-separated list of columns to export")
    parser.add_argument("--verbose", action="store_true", help="Enable verbose logging")

    args = parser.parse_args()

    # Configure logging
    logging.basicConfig(
        level=logging.DEBUG if args.verbose else logging.INFO,
        format="%(asctime)s %(levelname)-8s %(name)s: %(message)s"
    )

    # Load input data
    with open(args.input, 'r', encoding='utf-8') as f:
        records = json.load(f)

    # Parse columns if provided
    columns = None
    if args.columns:
        columns = [c.strip() for c in args.columns.split(',')]

    # Export to CSV
    export_usage_to_csv(records, args.output, columns)


if __name__ == "__main__":
    main()
