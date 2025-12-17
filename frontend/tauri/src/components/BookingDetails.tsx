import React, { useEffect, useMemo, useRef, useState } from "react";
import { X, Download, Train, MapPin, Users, CreditCard, FolderOpen, CheckCircle } from "lucide-react";
import { jsPDF } from "jspdf";
import { writeFile, BaseDirectory } from "@tauri-apps/plugin-fs";
import { revealItemInDir } from "@tauri-apps/plugin-opener";
import { downloadDir, join } from "@tauri-apps/api/path";
import { getBookingReceipt, ReceiptDTO } from "../services/bookingService";
import "./BookingDetails.css";

interface BookingDetailsProps {
    referenceCode: string;
    onClose: () => void;
    bookingStatus?: string;
    onCancel?: () => void;
}

const BookingDetails: React.FC<BookingDetailsProps> = ({ referenceCode, onClose, bookingStatus, onCancel }) => {
    const [receipt, setReceipt] = useState<ReceiptDTO | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [downloading, setDownloading] = useState(false);
    const [downloadProgress, setDownloadProgress] = useState(0);
    const [savedFilePath, setSavedFilePath] = useState<string | null>(null);
    const downloadIntervalRef = useRef<number | null>(null);

    useEffect(() => {
        const fetchReceipt = async () => {
            try {
                const data = await getBookingReceipt(referenceCode);
                setReceipt(data);
            } catch (err) {
                setError("Failed to load booking details");
                console.error(err);
            } finally {
                setLoading(false);
            }
        };
        fetchReceipt();

        return () => {
            if (downloadIntervalRef.current) {
                window.clearInterval(downloadIntervalRef.current);
            }
        };
    }, [referenceCode]);

    const formatDateTime = useMemo(
        () => (dateString: string) => {
            const date = new Date(dateString);
            return {
                date: date.toLocaleDateString("fr-FR", {
                    weekday: "long",
                    day: "2-digit",
                    month: "long",
                    year: "numeric",
                }),
                time: date.toLocaleTimeString("fr-FR", {
                    hour: "2-digit",
                    minute: "2-digit",
                }),
            };
        },
        []
    );

    const formatCurrency = (amount: number) => `${amount.toFixed(2)} dh`;

    const generatePDFHtml = (data: ReceiptDTO) => {
        const departure = formatDateTime(data.departureTime);
        const arrival = formatDateTime(data.arrivalTime);
        const bookingDate = formatDateTime(data.bookingDate);

        return `
    <style>
        .pdf-container { font-family: 'Segoe UI', Arial, sans-serif; background: #f5f5f5; padding: 24px; width: 800px; }
        .pdf-container * { box-sizing: border-box; }
        .pdf-container .ticket { max-width: 820px; margin: 0 auto; background: white; border-radius: 18px; overflow: hidden; box-shadow: 0 12px 40px rgba(0,0,0,0.12); }
        .pdf-container .ticket-header { background: linear-gradient(135deg, #0056b3 0%, #004494 100%); color: white; padding: 32px; text-align: center; }
        .pdf-container .ticket-header h1 { font-size: 30px; letter-spacing: -0.5px; margin-bottom: 6px; }
        .pdf-container .ticket-header .subtitle { opacity: 0.9; font-size: 14px; letter-spacing: 1px; text-transform: uppercase; }
        .pdf-container .reference-banner { background: #ffc107; color: #212529; padding: 18px; text-align: center; font-weight: 700; font-size: 20px; letter-spacing: 0.5px; }
        .pdf-container .reference-banner span { display: inline-block; font-family: 'Courier New', monospace; font-size: 26px; margin-left: 10px; }
        .pdf-container .ticket-body { padding: 32px; }
        .pdf-container .journey-info { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; background: #f8f9fa; border-radius: 16px; padding: 24px; border: 1px solid rgba(0,86,179,0.08); }
        .pdf-container .station { text-align: center; flex: 1; }
        .pdf-container .station .city { font-size: 24px; font-weight: 700; color: #212529; margin-bottom: 6px; text-transform: uppercase; letter-spacing: 1px; }
        .pdf-container .station .time { font-size: 34px; font-weight: 700; color: #0056b3; }
        .pdf-container .station .date { font-size: 13px; color: #6c757d; margin-top: 6px; text-transform: capitalize; }
        .pdf-container .journey-arrow { flex: 0 0 110px; text-align: center; color: #0056b3; font-size: 36px; font-weight: 700; }
        .pdf-container .journey-arrow .train-name { font-size: 14px; color: #6c757d; margin-top: 12px; text-transform: uppercase; letter-spacing: 2px; }
        .pdf-container .details-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(230px, 1fr)); gap: 18px; margin-bottom: 30px; }
        .pdf-container .detail-card { background: #f8f9fa; padding: 20px; border-radius: 12px; border: 1px solid rgba(0,0,0,0.05); }
        .pdf-container .detail-card .label { font-size: 12px; color: #6c757d; text-transform: uppercase; letter-spacing: 1px; margin-bottom: 10px; }
        .pdf-container .detail-card .value { font-size: 16px; font-weight: 600; color: #212529; }
        .pdf-container .detail-card .value.mono { font-family: 'Courier New', monospace; letter-spacing: 1px; font-size: 17px; }
        .pdf-container .passengers-section { margin-bottom: 32px; }
        .pdf-container .passengers-section h3 { font-size: 18px; color: #212529; margin-bottom: 18px; border-bottom: 2px solid rgba(0,86,179,0.2); padding-bottom: 10px; display: inline-block; }
        .pdf-container .passenger-list { display: flex; flex-wrap: wrap; gap: 12px; }
        .pdf-container .passenger-chip { background: linear-gradient(135deg, rgba(0,86,179,0.08), rgba(0,86,179,0.15)); color: #0056b3; padding: 10px 18px; border-radius: 24px; font-weight: 600; font-size: 15px; }
        .pdf-container .total-section { background: linear-gradient(135deg, #0056b3 0%, #004494 100%); color: white; border-radius: 14px; padding: 24px 28px; display: flex; align-items: center; justify-content: space-between; box-shadow: 0 10px 25px rgba(0,86,179,0.3); }
        .pdf-container .total-section .label { font-size: 18px; text-transform: uppercase; letter-spacing: 1px; }
        .pdf-container .total-section .amount { font-size: 36px; font-weight: 700; }
        .pdf-container .ticket-footer { text-align: center; padding: 24px; background: #f8f9fa; color: #6c757d; font-size: 13px; }
        .pdf-container .ticket-footer .note { font-style: italic; margin-top: 8px; }
        .pdf-container .qr-placeholder { width: 120px; height: 120px; background: repeating-linear-gradient(45deg, #000, #000 5px, #fff 5px, #fff 10px); margin: 22px auto; border-radius: 12px; display: flex; align-items: center; justify-content: center; color: rgba(255,255,255,0.85); font-size: 12px; font-weight: bold; text-transform: uppercase; letter-spacing: 2px; }
    </style>
    <div class="pdf-container">
        <div class="ticket">
            <div class="ticket-header">
                <h1>MarkoubClone</h1>
                <div class="subtitle">Billet de voyage</div>
            </div>
            <div class="reference-banner">
                Référence réservation<span>${data.bookingReference}</span>
            </div>
            <div class="ticket-body">
                <div class="journey-info">
                    <div class="station">
                        <div class="city">${data.departureStation}</div>
                        <div class="time">${departure.time}</div>
                        <div class="date">${departure.date}</div>
                    </div>
                    <div class="journey-arrow">
                        →
                        <div class="train-name">${data.trainName}</div>
                    </div>
                    <div class="station">
                        <div class="city">${data.arrivalStation}</div>
                        <div class="time">${arrival.time}</div>
                        <div class="date">${arrival.date}</div>
                    </div>
                </div>

                <div class="details-grid">
                    <div class="detail-card">
                        <div class="label">Date de réservation</div>
                        <div class="value">${bookingDate.date}</div>
                    </div>
                    <div class="detail-card">
                        <div class="label">Statut du paiement</div>
                        <div class="value">${data.paymentStatus}</div>
                    </div>
                    <div class="detail-card">
                        <div class="label">Transaction</div>
                        <div class="value mono">${data.transactionId || "N/A"}</div>
                    </div>
                    <div class="detail-card">
                        <div class="label">Nombre de passagers</div>
                        <div class="value">${data.passengerNames.length}</div>
                    </div>
                </div>

                <div class="passengers-section">
                    <h3>Passagers</h3>
                    <div class="passenger-list">
                        ${data.passengerNames.map((name) => `<div class="passenger-chip">${name}</div>`).join("")}
                    </div>
                </div>

                <div class="total-section">
                    <div class="label">Montant total</div>
                    <div class="amount">${formatCurrency(data.totalAmount)}</div>
                </div>
            </div>

            <div class="ticket-footer">
                <div class="qr-placeholder">QR</div>
                <p>Présentez ce ticket lors de l'embarquement</p>
                <p class="note">Merci d'avoir choisi MarkoubClone</p>
            </div>
        </div>
    </div>`;
    };

    const handleDownload = async () => {
        if (!receipt || downloading) return;

        setDownloading(true);
        setDownloadProgress(0);
        setSavedFilePath(null);

        // Simulate download progress for better UX
        let progress = 0;
        downloadIntervalRef.current = window.setInterval(() => {
            progress += Math.random() * 20;
            if (progress >= 90) {
                progress = 90;
            }
            setDownloadProgress(Math.floor(progress));
        }, 200);

        try {
            const htmlContent = generatePDFHtml(receipt);

            // Create a temporary container
            const container = document.createElement("div");
            container.innerHTML = htmlContent;
            container.style.position = "absolute";
            container.style.left = "-9999px";
            container.style.top = "0";
            document.body.appendChild(container);

            const doc = new jsPDF({
                orientation: "portrait",
                unit: "pt",
                format: "a4",
            });

            await doc.html(container.querySelector(".pdf-container") as HTMLElement, {
                callback: async function (doc) {
                    try {
                        const pdfData = doc.output("arraybuffer");
                        const fileName = `ticket-${receipt.bookingReference}.pdf`;

                        // Save using Tauri FS
                        await writeFile(fileName, new Uint8Array(pdfData), { baseDir: BaseDirectory.Download });

                        // Get absolute path for "Show in Explorer"
                        const downloadDirPath = await downloadDir();
                        const filePath = await join(downloadDirPath, fileName);

                        setSavedFilePath(filePath);

                        // Cleanup
                        document.body.removeChild(container);
                        setDownloadProgress(100);
                        setTimeout(() => {
                            setDownloading(false);
                            setDownloadProgress(0);
                            if (downloadIntervalRef.current) {
                                window.clearInterval(downloadIntervalRef.current);
                                downloadIntervalRef.current = null;
                            }
                        }, 500);
                    } catch (e) {
                        console.error("Save error", e);
                        // Fallback to browser save if Tauri fails
                        doc.save(`ticket-${receipt.bookingReference}.pdf`);
                        document.body.removeChild(container);
                        setDownloading(false);
                        setDownloadProgress(0);
                        if (downloadIntervalRef.current) {
                            window.clearInterval(downloadIntervalRef.current);
                            downloadIntervalRef.current = null;
                        }
                    }
                },
                x: 10,
                y: 10,
                width: 575, // A4 width in pt (595) - margins
                windowWidth: 800, // The width of the element to render
            });
        } catch (err) {
            console.error(err);
            setError("Erreur lors du téléchargement du ticket");
            setDownloading(false);
            setDownloadProgress(0);
            if (downloadIntervalRef.current) {
                window.clearInterval(downloadIntervalRef.current);
                downloadIntervalRef.current = null;
            }
        }
    };

    const handleViewInExplorer = async () => {
        if (savedFilePath) {
            await revealItemInDir(savedFilePath);
        }
    };

    if (loading) {
        return (
            <div className="modal-overlay">
                <div className="modal-content">
                    <div className="loading-state">Chargement des détails...</div>
                </div>
            </div>
        );
    }

    if (error || !receipt) {
        return (
            <div className="modal-overlay">
                <div className="modal-content">
                    <div className="error-state">
                        <p>{error || "Error loading details"}</p>
                        <button onClick={onClose}>Fermer</button>
                    </div>
                </div>
            </div>
        );
    }

    const departure = formatDateTime(receipt.departureTime);
    const arrival = formatDateTime(receipt.arrivalTime);

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content booking-details-modal" onClick={(e) => e.stopPropagation()}>
                <button className="modal-close" onClick={onClose}>
                    <X size={24} />
                </button>

                <div className="details-header">
                    <div className="header-content">
                        <h2>Détails de la Réservation</h2>
                        <span className="reference">#{receipt.bookingReference}</span>
                    </div>
                    <div className="header-actions" style={{ display: "flex", gap: "10px", marginRight: "40px" }}>
                        {(bookingStatus === "CONFIRMED" || bookingStatus === "PENDING_PAYMENT") && onCancel && (
                            <button
                                className="header-download-btn"
                                onClick={onCancel}
                                style={{
                                    marginRight: 0,
                                    backgroundColor: "rgba(239, 68, 68, 0.2)",
                                    borderColor: "rgba(239, 68, 68, 0.5)",
                                }}
                            >
                                <X size={18} />
                                <span>Annuler</span>
                            </button>
                        )}
                        <button
                            className="header-download-btn"
                            onClick={handleDownload}
                            disabled={downloading}
                            style={{ marginRight: 0 }}
                        >
                            <Download size={18} />
                            <span>{downloading ? "..." : "Télécharger"}</span>
                        </button>
                    </div>
                </div>

                <div className="journey-visual">
                    <div className="journey-station">
                        <MapPin size={20} className="station-icon departure" />
                        <div className="station-info">
                            <span className="station-name">{receipt.departureStation}</span>
                            <span className="station-time">{departure.time}</span>
                            <span className="station-date">{departure.date}</span>
                        </div>
                    </div>
                    <div className="journey-line">
                        <Train size={24} className="train-icon" />
                        <span className="train-name">{receipt.trainName}</span>
                    </div>
                    <div className="journey-station">
                        <MapPin size={20} className="station-icon arrival" />
                        <div className="station-info">
                            <span className="station-name">{receipt.arrivalStation}</span>
                            <span className="station-time">{arrival.time}</span>
                            <span className="station-date">{arrival.date}</span>
                        </div>
                    </div>
                </div>

                <div className="details-section">
                    <h3>
                        <Users size={18} /> Passagers ({receipt.passengerNames.length})
                    </h3>
                    <div className="passengers-list">
                        {receipt.passengerNames.map((name, index) => (
                            <span key={index} className="passenger-tag">
                                {name}
                            </span>
                        ))}
                    </div>
                </div>

                <div className="details-section">
                    <h3>
                        <CreditCard size={18} /> Paiement
                    </h3>
                    <div className="payment-info">
                        <div className="payment-row">
                            <span>Statut</span>
                            <span className={`payment-status ${receipt.paymentStatus.toLowerCase()}`}>
                                {receipt.paymentStatus}
                            </span>
                        </div>
                        {receipt.transactionId && (
                            <div className="payment-row">
                                <span>Transaction ID</span>
                                <span className="transaction-id">{receipt.transactionId}</span>
                            </div>
                        )}
                    </div>
                </div>

                <div className="total-amount">
                    <span>Montant Total</span>
                    <span className="amount">{formatCurrency(receipt.totalAmount)}</span>
                </div>

                {downloading && (
                    <div className="download-progress">
                        <div className="progress-bar">
                            <div className="progress" style={{ width: `${downloadProgress}%` }} />
                        </div>
                        <span className="progress-label">{downloadProgress}%</span>
                    </div>
                )}

                {savedFilePath && (
                    <div className="success-popup-overlay">
                        <div className="download-success-popup">
                            <button className="popup-close" onClick={() => setSavedFilePath(null)}>
                                <X size={20} />
                            </button>
                            <div className="success-icon-large">
                                <CheckCircle size={48} />
                            </div>
                            <h3>Téléchargement réussi !</h3>
                            <p>Votre ticket a été enregistré dans les téléchargements.</p>
                            <button className="view-btn primary" onClick={handleViewInExplorer}>
                                <FolderOpen size={18} />
                                Ouvrir le dossier
                            </button>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default BookingDetails;
