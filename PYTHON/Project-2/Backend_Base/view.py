import httpx
import uuid
from rich.console import Console
from rich.table import Table
from rich.panel import Panel
from rich.prompt import Prompt

console = Console()
API_URL = "http://localhost:8000/api"

def format_currency(cents: int) -> str:
    """DISPLAY LOGIC: Converting cents back to human-readable dollars"""
    return f"${cents / 100:.2f}"

def view_ledger():
    """Fetches data from the Model and presents it in an awesome UI"""
    try:
        response = httpx.get(f"{API_URL}/ledger", timeout=15.0)
        response.raise_for_status()
        data = response.json()
    except Exception:
        console.print("[bold red]Error connecting to Logic Engine.[/bold red]")
        return

    # Building the Awesome UI Table
    table = Table(title="Enterprise Audit Trail", show_header=True, header_style="bold magenta")
    table.add_column("DB_ID", style="dim", width=26)
    table.add_column("Description")
    table.add_column("Status", justify="center", style="bold green")
    table.add_column("Amount", justify="right", style="bold cyan")

    for tx in data["transactions"]:
        table.add_row(
            tx["id"], 
            tx["description"], 
            tx["status"], 
            format_currency(tx["amountInCents"])
        )
    
    console.print(table)
    
    # Total Panel
    total_spent = format_currency(data["summary"]["totalSpentCents"])
    console.print(Panel(f"[bold white]TOTAL OUTPUT:[/bold white] [bold cyan]{total_spent}[/bold cyan]", expand=False))

def add_transaction():
    """Takes input and sends to Logic Layer"""
    console.print("\n[bold yellow]--- New Transaction ---[/bold yellow]")
    amount_str = Prompt.ask("Enter amount (e.g., 50.00)")
    description = Prompt.ask("Enter description")
    
    try:
        payload = {
            "amount": float(amount_str),
            "description": description,
            "idempotencyKey": str(uuid.uuid4()) # Prevents accidental double-submission
        }
        
        response = httpx.post(f"{API_URL}/expenses", json=payload, timeout=15.0)
        
        if response.status_code == 200:
            console.print("[bold green]✔ Transaction Authorized & Persisted.[/bold green]")
        else:
            console.print(f"[bold red]✘ Error:[/bold red] {response.json().get('detail')}")
            
    except ValueError:
        console.print("[bold red]✘ Invalid format. Please enter numbers only.[/bold red]")

def main():
    """The View Controller"""
    while True:
        console.print("\n[1] Add Expense  [2] View Ledger  [3] Exit")
        choice = Prompt.ask("Select operation", choices=["1", "2", "3"])
        
        if choice == "1":
            add_transaction()
        elif choice == "2":
            view_ledger()
        elif choice == "3":
            console.print("[bold green]System shutting down gracefully. Goodbye![/bold green]")
            break

if __name__ == "__main__":
    main()