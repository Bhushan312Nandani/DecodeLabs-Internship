import React, { useState, useEffect } from 'react';
import { CreditCard, DollarSign, Activity, AlertCircle, CheckCircle2, Trash2, Edit2, X, Save } from 'lucide-react';

const API_URL = "http://localhost:8000/api";

export default function App() {
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');
  const [ledger, setLedger] = useState({ transactions: [], summary: { totalSpentCents: 0 } });
  const [error, setError] = useState(null);
  
  // New State for Editing
  const [editingId, setEditingId] = useState(null);
  const [editDescription, setEditDescription] = useState('');

  useEffect(() => {
    fetchLedger();
  }, []);

  const fetchLedger = async () => {
    try {
      const response = await fetch(`${API_URL}/ledger`);
      const data = await response.json();
      setLedger(data);
    } catch (err) {
      setError("Failed to connect to the Logic Engine.");
    }
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    setError(null);
    const idempotencyKey = crypto.randomUUID(); 

    try {
      const response = await fetch(`${API_URL}/expenses`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ amount: parseFloat(amount), description, idempotencyKey })
      });

      if (!response.ok) throw new Error("Transaction Failed");
      setAmount('');
      setDescription('');
      fetchLedger();
    } catch (err) {
      setError(err.message);
    }
  };

  // UPDATE LOGIC
  const handleUpdate = async (id) => {
    try {
      const response = await fetch(`${API_URL}/expenses/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ description: editDescription })
      });
      if (!response.ok) throw new Error("Update Failed");
      setEditingId(null);
      fetchLedger();
    } catch (err) {
      setError(err.message);
    }
  };

  // DELETE (VOID) LOGIC
  const handleVoid = async (id) => {
    try {
      const response = await fetch(`${API_URL}/expenses/${id}`, { method: 'DELETE' });
      if (!response.ok) throw new Error("Void Action Failed");
      fetchLedger();
    } catch (err) {
      setError(err.message);
    }
  };

  const formatCurrency = (cents) => `$${(cents / 100).toFixed(2)}`;

  return (
    <div className="min-h-screen bg-slate-50 font-sans p-4 md:p-8">
      <div className="max-w-5xl mx-auto">
        
        <header className="mb-8 flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <h1 className="text-3xl font-bold text-slate-800 flex items-center gap-3">
              <CreditCard className="text-blue-600 h-8 w-8" /> 
              Enterprise Ledger
            </h1>
            <p className="text-slate-500 mt-1">Full CRUD enabled with Immutable Audit Trail</p>
          </div>
          <div className="bg-blue-600 text-white px-6 py-4 rounded-2xl shadow-lg shadow-blue-200 flex items-center gap-3">
            <Activity className="h-5 w-5 opacity-80" />
            <div className="flex flex-col">
              <span className="text-xs font-semibold uppercase tracking-wider opacity-80">Total Output</span>
              <span className="text-2xl font-bold leading-none mt-1">
                {formatCurrency(ledger.summary.totalSpentCents)}
              </span>
            </div>
          </div>
        </header>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          
          <div className="lg:col-span-1">
            <div className="bg-white p-6 rounded-3xl shadow-sm border border-slate-100 sticky top-8">
              <h2 className="text-lg font-bold mb-6 text-slate-800">New Transaction</h2>
              <form onSubmit={handleCreate} className="space-y-5">
                {error && (
                  <div className="flex items-start gap-2 text-red-600 text-sm bg-red-50 p-4 rounded-xl">
                    <AlertCircle className="h-5 w-5 shrink-0" />
                    <p>{error}</p>
                  </div>
                )}
                <div>
                  <label className="block text-sm font-semibold text-slate-600 mb-2">Amount</label>
                  <div className="relative">
                    <span className="absolute left-4 top-3.5 text-slate-400"><DollarSign size={20}/></span>
                    <input type="number" step="0.01" value={amount} onChange={(e) => setAmount(e.target.value)} required
                      className="w-full pl-11 pr-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:bg-white focus:ring-2 focus:ring-blue-500 outline-none transition-all font-medium" placeholder="0.00" />
                  </div>
                </div>
                <div>
                  <label className="block text-sm font-semibold text-slate-600 mb-2">Description</label>
                  <input type="text" value={description} onChange={(e) => setDescription(e.target.value)} required
                    className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:bg-white focus:ring-2 focus:ring-blue-500 outline-none transition-all font-medium" placeholder="e.g., Server Hosting" />
                </div>
                <button type="submit" className="w-full bg-slate-800 hover:bg-slate-900 text-white font-semibold py-3.5 rounded-xl transition-colors shadow-md mt-2">
                  Authorize & Persist
                </button>
              </form>
            </div>
          </div>

          <div className="lg:col-span-2">
            <div className="bg-white p-6 rounded-3xl shadow-sm border border-slate-100 min-h-[400px]">
              <h2 className="text-lg font-bold mb-6 text-slate-800">Immutable Audit Trail</h2>
              
              <div className="space-y-3">
                {ledger.transactions.map((tx) => {
                  const isVoided = tx.status === "VOIDED";
                  
                  return (
                    <div key={tx.id} className={`flex flex-col sm:flex-row sm:items-center justify-between p-4 rounded-2xl border transition-all group ${isVoided ? 'bg-slate-50/50 border-slate-100 opacity-60' : 'bg-white border-slate-100 hover:border-blue-200 hover:shadow-sm'}`}>
                      
                      <div className="flex-1">
                        {/* INLINE EDITING MODE */}
                        {editingId === tx.id ? (
                          <div className="flex items-center gap-2 mb-2 sm:mb-0">
                            <input autoFocus type="text" value={editDescription} onChange={(e) => setEditDescription(e.target.value)}
                              className="px-3 py-1.5 text-sm border-2 border-blue-500 rounded-lg outline-none w-full max-w-[200px]" />
                            <button onClick={() => handleUpdate(tx.id)} className="p-1.5 bg-blue-100 text-blue-700 hover:bg-blue-200 rounded-lg"><Save size={16} /></button>
                            <button onClick={() => setEditingId(null)} className="p-1.5 bg-slate-100 text-slate-600 hover:bg-slate-200 rounded-lg"><X size={16} /></button>
                          </div>
                        ) : (
                          <>
                            <p className={`font-semibold text-slate-800 ${isVoided ? 'line-through text-slate-500' : ''}`}>{tx.description}</p>
                            <p className="text-[11px] text-slate-400 mt-1 font-mono">ID: {tx.id.substring(0, 18)}...</p>
                          </>
                        )}
                      </div>

                      <div className="flex items-center justify-between sm:justify-end gap-6 mt-3 sm:mt-0">
                        {/* HOVER ACTIONS */}
                        {!isVoided && editingId !== tx.id && (
                          <div className="flex items-center gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                            <button onClick={() => { setEditingId(tx.id); setEditDescription(tx.description); }} className="p-2 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors">
                              <Edit2 size={16} />
                            </button>
                            <button onClick={() => handleVoid(tx.id)} className="p-2 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors">
                              <Trash2 size={16} />
                            </button>
                          </div>
                        )}

                        <div className="text-right flex flex-col items-end min-w-[80px]">
                          <p className={`font-bold text-lg ${isVoided ? 'text-slate-400 line-through' : 'text-slate-800'}`}>
                            {formatCurrency(tx.amountInCents)}
                          </p>
                          <span className={`text-[10px] font-bold uppercase tracking-wider px-2.5 py-0.5 rounded-full mt-1 ${isVoided ? 'text-red-600 bg-red-100' : 'text-emerald-600 bg-emerald-100/80'}`}>
                            {tx.status}
                          </span>
                        </div>
                      </div>

                    </div>
                  );
                })}
              </div>
            </div>
          </div>

        </div>
      </div>
    </div>
  );
}